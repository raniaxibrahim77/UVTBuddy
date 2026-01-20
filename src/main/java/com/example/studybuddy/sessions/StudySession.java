package com.example.studybuddy.sessions;

import com.example.studybuddy.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudySession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String course;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer capacity = 2;

    @Column(length = 10)
    private String language;

    private String locationText;

    @ManyToOne(optional = false)
    private User creator;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (capacity == null) capacity = 2;
    }
}
