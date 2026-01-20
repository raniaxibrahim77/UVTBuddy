package com.example.studybuddy.events;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventsSeeder implements CommandLineRunner {

    private final EventRepository events;

    public EventsSeeder(EventRepository events) {
        this.events = events;
    }

    @Override
    public void run(String... args) {
        if (events.count() > 0) return; // avoid duplicates on restart

        // 1️⃣ Culture Day – Faculty of Arts and Design
        events.save(new Event(
                "Culture Reimagined – National Culture Day",
                "Event organized by the Faculty of Arts and Design UVT, dedicated to celebrating National Culture Day through contemporary artistic perspectives.",
                LocalDateTime.of(2026, 1, 19, 10, 0),
                LocalDateTime.of(2026, 1, 19, 14, 0),
                "UVT – Faculty of Arts and Design"
        ));

        // 2️⃣ Digital Pedagogy Conference
        events.save(new Event(
                "Digital Pedagogy Conference – INO-VEST DigiPedia",
                "Conference presenting innovative pedagogical models and educational technologies for pre-university teachers.",
                LocalDateTime.of(2026, 2, 29, 9, 30),
                LocalDateTime.of(2026, 2, 29, 16, 0),
                "UVT Conference Hall"
        ));

        // 3️⃣ Culture Marathon
        events.save(new Event(
                "Culture Marathon – UVT",
                "A full-day cultural marathon proposed by West University of Timișoara, featuring conferences, debates, and artistic moments.",
                LocalDateTime.of(2026, 3, 2, 10, 0),
                LocalDateTime.of(2026, 3, 2, 18, 0),
                "UVT Campus"
        ));

        // 4️⃣ Made-up StudyBuddy Event (nice for demo)
        events.save(new Event(
                "StudyBuddy: Exam Prep Meetup",
                "Student-organized study session for exam preparation and peer support.",
                LocalDateTime.of(2026, 3, 5, 16, 0),
                LocalDateTime.of(2026, 3, 5, 18, 0),
                "Library – Study Room 3"
        ));
    }
}
