package com.example.studybuddy.sessions;

import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudySessionControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepo;
    @Autowired StudySessionRepository sessionRepo;
    @Autowired StudySessionParticipantRepository participantRepo;

    @BeforeEach
    void setup() {
        participantRepo.deleteAll();
        sessionRepo.deleteAll();
        userRepo.deleteAll();
    }

    private User saveUser(String email) {
        return userRepo.save(User.builder()
                .name("Test " + email)
                .email(email)
                .passwordHash("hashed") // not used because we use WithMockUser
                .role(User.Role.STUDENT)
                .build());
    }

    private Map<String, Object> createReq(String title, String course, int capacity) {
        return Map.of(
                "title", title,
                "course", course,
                "description", "Unit test session",
                "startsAt", LocalDateTime.now().plusDays(1).withNano(0).toString(),
                "durationMinutes", 60,
                "capacity", capacity,
                "language", "EN",
                "locationText", "Library"
        );
    }

    @Test
    @WithMockUser(username = "creator@test.com")
    void createSession_createsSession_andAutoJoinsCreator() throws Exception {
        saveUser("creator@test.com");

        var body = objectMapper.writeValueAsString(createReq("Math Study", "PTS", 5));

        // Create session
        String createdJson = mvc.perform(post("/api/sessions")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title").value("Math Study"))
                .andExpect(jsonPath("$.course").value("PTS"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long sessionId = objectMapper.readTree(createdJson).get("id").asLong();

        // Creator should be auto-joined -> stats joined = 1
        mvc.perform(get("/api/sessions/{id}/stats", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(5))
                .andExpect(jsonPath("$.joined").value(1));
    }

    @Test
    @WithMockUser(username = "user2@test.com")
    void joinSession_addsParticipant_andIncreasesJoinedCount() throws Exception {
        // Create users
        User creator = saveUser("creator@test.com");
        saveUser("user2@test.com");

        // Create a session directly in DB as creator
        StudySession session = sessionRepo.save(StudySession.builder()
                .title("Study")
                .course("PTS")
                .startsAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .capacity(5)
                .creator(creator)
                .build());

        // Auto-join creator like controller does
        participantRepo.save(StudySessionParticipant.builder()
                .session(session)
                .user(creator)
                .status(ParticipationStatus.JOINED)
                .build());

        // Join as user2
        mvc.perform(post("/api/sessions/{id}/join", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message", anyOf(is("Joined"), is("Re-joined"), is("Already joined"))));

        // joined should now be 2
        mvc.perform(get("/api/sessions/{id}/stats", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joined").value(2));
    }

    @Test
    @WithMockUser(username = "user2@test.com")
    void leaveSession_cancelsParticipation_andDecreasesJoinedCount() throws Exception {
        // Create users
        User creator = saveUser("creator@test.com");
        User user2 = saveUser("user2@test.com");

        // Create session
        StudySession session = sessionRepo.save(StudySession.builder()
                .title("Study")
                .course("PTS")
                .startsAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .capacity(5)
                .creator(creator)
                .build());

        // Creator joined + user2 joined
        participantRepo.save(StudySessionParticipant.builder()
                .session(session)
                .user(creator)
                .status(ParticipationStatus.JOINED)
                .build());

        participantRepo.save(StudySessionParticipant.builder()
                .session(session)
                .user(user2)
                .status(ParticipationStatus.JOINED)
                .build());

        // Sanity: joined = 2
        mvc.perform(get("/api/sessions/{id}/stats", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joined").value(2));

        // Leave as user2
        mvc.perform(post("/api/sessions/{id}/leave", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message", anyOf(is("Left"), is("Already left"))));

        // joined should now be 1
        mvc.perform(get("/api/sessions/{id}/stats", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joined").value(1));
    }
}
