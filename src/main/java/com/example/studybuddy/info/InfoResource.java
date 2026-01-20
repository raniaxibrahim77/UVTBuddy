package com.example.studybuddy.info;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class InfoResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(length = 2000)
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InfoCategory category;

    @Enumerated(EnumType.STRING)
    private Location location;

    private String link;

    public InfoResource() {}

    public InfoResource(String title, String content, InfoCategory category, Location location, String link) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.location = location;
        this.link = link;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public InfoCategory getCategory() { return category; }
    public Location getLocation() { return location; }
    public String getLink() { return link; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCategory(InfoCategory category) { this.category = category; }
    public void setLocation(Location location) { this.location = location; }
    public void setLink(String link) { this.link = link; }
}
