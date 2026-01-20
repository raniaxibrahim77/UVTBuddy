package com.example.studybuddy.sessions;

import com.example.studybuddy.users.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionParticipantTest {

    @Test
    void noArgsConstructor_createsObject() {
        StudySessionParticipant p = new StudySessionParticipant();
        assertNotNull(p);
    }

    @Test
    void allArgsConstructor_setsFieldsCorrectly() {
        StudySession session = new StudySession();
        User user = new User();

        LocalDateTime joinedAt = LocalDateTime.now().minusMinutes(5);

        StudySessionParticipant p = new StudySessionParticipant(
                1L,
                session,
                user,
                ParticipationStatus.CANCELED,
                joinedAt
        );

        assertEquals(1L, p.getId());
        assertSame(session, p.getSession());
        assertSame(user, p.getUser());
        assertEquals(ParticipationStatus.CANCELED, p.getStatus());
        assertEquals(joinedAt, p.getJoinedAt());
    }

    @Test
    void prePersist_setsDefaultsWhenNull() {
        StudySessionParticipant p = new StudySessionParticipant();
        p.setJoinedAt(null);
        p.setStatus(null);

        p.prePersist();

        assertNotNull(p.getJoinedAt());
        assertEquals(ParticipationStatus.JOINED, p.getStatus());
    }

}
