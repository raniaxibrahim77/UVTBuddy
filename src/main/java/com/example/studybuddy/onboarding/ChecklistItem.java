package com.example.studybuddy.onboarding;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable=false)
    private Integer sortOrder;

    @Column(length = 10, nullable = false)
    private String language;
}
