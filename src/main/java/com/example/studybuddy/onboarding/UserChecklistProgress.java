package com.example.studybuddy.onboarding;

import com.example.studybuddy.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "item_id"}))
public class UserChecklistProgress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private User user;

    @ManyToOne(optional=false)
    private ChecklistItem item;

    @Column(nullable=false)
    private boolean done;

    private LocalDateTime doneAt;
}
