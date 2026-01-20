package com.example.studybuddy.sessions;

import java.time.LocalDateTime;

public record SessionListItem(
        Long id,
        String title,
        String course,
        String description,
        LocalDateTime startsAt,
        Integer durationMinutes,
        Integer capacity,
        String language,
        String locationText,
        CreatorView creator,
        long joinedCount,
        boolean joinedByMe
) {
    public record CreatorView(Long id, String name, String email) {}
}
