package com.example.studybuddy.sessions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByStartsAtAfterOrderByStartsAtAsc(LocalDateTime after);

    List<StudySession> findByCourseIgnoreCaseAndStartsAtAfterOrderByStartsAtAsc(
            String course,
            LocalDateTime after
    );
}
