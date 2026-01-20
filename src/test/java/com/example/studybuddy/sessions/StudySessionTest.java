package com.example.studybuddy.sessions;

import com.example.studybuddy.users.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionTest {

    @Test
    void studySessionConstructor_setsFieldsCorrectly() {
        User creator = User.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash("hashed")
                .role(User.Role.STUDENT)
                .build();

        StudySession s = new StudySession(
                null,
                "Math Study",
                "PTS",
                null,
                LocalDateTime.now().plusDays(1),
                60,
                5,
                null,
                null,
                creator,
                null
        );

        assertEquals("Math Study", s.getTitle());
        assertEquals("PTS", s.getCourse());
        assertEquals(60, s.getDurationMinutes());
        assertEquals(5, s.getCapacity());
        assertSame(creator, s.getCreator());
    }

    @Test
    void prePersist_setsDefaultsWhenNull() {
        StudySession s = new StudySession();
        s.setCreatedAt(null);
        s.setCapacity(null);

        s.prePersist();

        assertNotNull(s.getCreatedAt());
        assertEquals(2, s.getCapacity());
    }

}
