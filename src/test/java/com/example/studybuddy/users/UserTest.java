package com.example.studybuddy.users;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void noArgsConstructor_createsObject() {
        assertNotNull(new User());
    }

    @Test
    void allArgsConstructor_setsFieldsCorrectly() {
        User u = new User(1L, "A", "a@b.com", "hash", "CS", "Any", User.Role.ADMIN);
        assertEquals("A", u.getName());
        assertEquals(User.Role.ADMIN, u.getRole());
    }
}
