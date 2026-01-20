package com.example.studybuddy.errors;

public class UserNotFound extends RuntimeException {
    public UserNotFound(Long id) {
        super("User not found: " + id);
    }
}
