package com.example.studybuddy.sessions;

import com.example.studybuddy.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "user_id"})
})
public class StudySessionParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudySession session;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status = ParticipationStatus.JOINED;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @PrePersist
    void prePersist() {
        if (joinedAt == null) joinedAt = LocalDateTime.now();
        if (status == null) status = ParticipationStatus.JOINED;
    }
}
