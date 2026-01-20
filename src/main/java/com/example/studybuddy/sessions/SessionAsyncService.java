package com.example.studybuddy.sessions;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SessionAsyncService {

    @Async
    public void afterSessionCreated(Long sessionId, String creatorEmail) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Async task finished for sessionId=" + sessionId
                + " created by " + creatorEmail
                + " (thread=" + Thread.currentThread().getName() + ")");
    }
}
