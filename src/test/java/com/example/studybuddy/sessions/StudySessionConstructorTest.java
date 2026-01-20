package com.example.studybuddy.sessions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionConstructorTest {

    @Test
    void noArgsConstructor_createsObject() {
        StudySession s = new StudySession();
        assertNotNull(s);
    }
}
