package com.example.studybuddy.sessions;

import com.example.studybuddy.errors.UnauthorizedException;
import com.example.studybuddy.errors.SessionNotFound;
import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDateTime;
import java.util.List;
import java.security.Principal;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionRepository sessionRepo;
    private final StudySessionParticipantRepository participantRepo;
    private final UserRepository userRepo;
    private final SessionAsyncService asyncService;

    @GetMapping
    public List<SessionListItem> listUpcoming(
            @RequestParam(required = false) String course,
            Principal principal
    ) {
        List<StudySession> sessions;

        if (course != null && !course.isBlank()) {
            sessions = sessionRepo.findByCourseIgnoreCaseAndStartsAtAfterOrderByStartsAtAsc(
                    course.trim(),
                    LocalDateTime.now()
            );
        } else {
            sessions = sessionRepo.findByStartsAtAfterOrderByStartsAtAsc(LocalDateTime.now());
        }

        var ids = sessions.stream().map(StudySession::getId).toList();

        final var counts = participantRepo.joinedCounts(ids).stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        final java.util.Set<Long> joinedByMe =
                (principal == null)
                        ? java.util.Collections.emptySet()
                        : new java.util.HashSet<>(
                        participantRepo.joinedSessionIdsForUser(
                                userRepo.findByEmail(principal.getName())
                                        .orElseThrow(() -> new UnauthorizedException("User not found for current login"))
                                        .getId(),
                                ids
                        )
                );

        return sessions.stream().map(s -> new SessionListItem(
                s.getId(),
                s.getTitle(),
                s.getCourse(),
                s.getDescription(),
                s.getStartsAt(),
                s.getDurationMinutes(),
                s.getCapacity(),
                s.getLanguage(),
                s.getLocationText(),
                s.getCreator() == null ? null : new SessionListItem.CreatorView(
                        s.getCreator().getId(),
                        s.getCreator().getName(),
                        s.getCreator().getEmail()
                ),
                counts.getOrDefault(s.getId(), 0L),
                joinedByMe.contains(s.getId())
        )).toList();
    }

    @GetMapping("/{id}")
    public StudySession getOne(@PathVariable Long id) {
        return sessionRepo.findById(id).orElseThrow(() -> new SessionNotFound(id));
    }


    @PostMapping
    public StudySession create(@Valid @RequestBody CreateSessionRequest req, Principal principal) {

        if (req.course() == null || req.course().isBlank()) {
            throw new IllegalArgumentException("Study session must include a course.");
        }

        User creator = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));

        StudySession session = StudySession.builder()
                .title(req.title().trim())
                .course(req.course().trim())
                .description(req.description())
                .startsAt(req.startsAt())
                .durationMinutes(req.durationMinutes())
                .capacity(req.capacity() == null ? 2 : req.capacity())
                .language(req.language())
                .locationText(req.locationText() == null ? null : req.locationText().trim())
                .creator(creator)
                .build();

        StudySession saved = sessionRepo.save(session);

        asyncService.afterSessionCreated(saved.getId(), creator.getEmail());

        participantRepo.save(StudySessionParticipant.builder()
                .session(saved)
                .user(creator)
                .status(ParticipationStatus.JOINED)
                .build());

        return saved;
    }

    @PostMapping("/{id}/join")
    public JoinResult join(@PathVariable Long id, Principal principal) {
        StudySession session = sessionRepo.findById(id).orElseThrow(() -> new SessionNotFound(id));
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));

        long joinedCount = participantRepo.countBySessionIdAndStatus(id, ParticipationStatus.JOINED);
        if (joinedCount >= session.getCapacity()) {
            return new JoinResult(false, "Session is full");
        }

        var existing = participantRepo.findBySessionIdAndUserId(id, user.getId());
        if (existing.isPresent()) {
            var p = existing.get();
            if (p.getStatus() == ParticipationStatus.JOINED) return new JoinResult(true, "Already joined");
            p.setStatus(ParticipationStatus.JOINED);
            participantRepo.save(p);
            return new JoinResult(true, "Re-joined");
        }

        participantRepo.save(StudySessionParticipant.builder()
                .session(session)
                .user(user)
                .status(ParticipationStatus.JOINED)
                .build());

        return new JoinResult(true, "Joined");
    }

    @PostMapping("/{id}/leave")
    public JoinResult leave(@PathVariable Long id, Principal principal) {
        StudySession session = sessionRepo.findById(id).orElseThrow(() -> new SessionNotFound(id));

        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));

        if (session.getCreator().getId().equals(user.getId())) {
            return new JoinResult(false, "Creator cannot leave their own session");
        }

        var existing = participantRepo.findBySessionIdAndUserId(id, user.getId());
        if (existing.isEmpty()) return new JoinResult(false, "Not a participant");

        var p = existing.get();
        if (p.getStatus() == ParticipationStatus.CANCELED) return new JoinResult(true, "Already left");

        p.setStatus(ParticipationStatus.CANCELED);
        participantRepo.save(p);
        return new JoinResult(true, "Left");
    }

    public record CreateSessionRequest(
            @NotBlank String title,

            String course,

            String description,

            @NotNull @Future LocalDateTime startsAt,

            @NotNull @Min(15) @Max(600) Integer durationMinutes,

            @Min(2) @Max(20) Integer capacity,

            @jakarta.validation.constraints.Size(max = 10)
            String language,

            @jakarta.validation.constraints.Size(max = 120)
            String locationText
    ) {}

    public record JoinResult(boolean ok, String message) {}

    @GetMapping("/{id}/participants")
    public List<ParticipantView> participants(@PathVariable Long id) {

        sessionRepo.findById(id).orElseThrow(() -> new SessionNotFound(id));

        return participantRepo.findBySessionIdAndStatus(id, ParticipationStatus.JOINED)
                .stream()
                .map(p -> new ParticipantView(
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getMajor(),
                        p.getJoinedAt()
                ))
                .toList();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!sessionRepo.existsById(id)) throw new SessionNotFound(id);

        participantRepo.deleteBySessionId(id);
        sessionRepo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public SessionStats stats(@PathVariable Long id) {
        StudySession s = sessionRepo.findById(id).orElseThrow(() -> new SessionNotFound(id));
        long joined = participantRepo.countBySessionIdAndStatus(id, ParticipationStatus.JOINED);
        return new SessionStats(s.getCapacity(), joined);
    }

    public record ParticipantView(Long id, String name, String major, java.time.LocalDateTime joinedAt) {}
    public record SessionStats(Integer capacity, long joined) {}

}

