package com.example.studybuddy.errors;

public class SessionNotFound extends RuntimeException {
    public SessionNotFound(Long id) {
        super("Session not found: " + id);
    }
}
