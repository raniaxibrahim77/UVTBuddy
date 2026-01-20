package com.example.studybuddy.onboarding;

import com.example.studybuddy.users.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserChecklistProgressTest {

    @Test
    void noArgsConstructor_createsObject() {
        assertNotNull(new UserChecklistProgress());
    }

    @Test
    void allArgsConstructor_setsFieldsCorrectly() {
        User u = new User();
        ChecklistItem item = new ChecklistItem();
        LocalDateTime doneAt = LocalDateTime.now();

        UserChecklistProgress p = new UserChecklistProgress(1L, u, item, true, doneAt);

        assertTrue(p.isDone());
        assertEquals(doneAt, p.getDoneAt());
        assertSame(u, p.getUser());
        assertSame(item, p.getItem());
    }
}
