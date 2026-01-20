package com.example.studybuddy.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String passwordHash;

    @Size(max = 80)
    private String major;

    @Size(max = 160)
    private String availability;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    public enum Role { STUDENT, ADMIN }

}
