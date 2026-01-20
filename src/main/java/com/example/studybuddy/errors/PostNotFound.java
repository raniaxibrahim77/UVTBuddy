package com.example.studybuddy.errors;

public class PostNotFound extends RuntimeException {
    public PostNotFound(Long id) {
        super("Post not found: " + id);
    }
}
