package com.example.studybuddy.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfoResourceTest {

    @Test
    void noArgsConstructor_createsObject() {
        assertNotNull(new InfoResource());
    }

    @Test
    void customConstructor_setsFieldsCorrectly() {
        InfoResource r = new InfoResource(
                "Title",
                "Content",
                InfoCategory.EVENTS,
                Location.GENERAL,
                "https://example.com"
        );

        assertEquals("Title", r.getTitle());
        assertEquals("Content", r.getContent());
        assertEquals(InfoCategory.EVENTS, r.getCategory());
        assertEquals(Location.GENERAL, r.getLocation());
        assertEquals("https://example.com", r.getLink());
    }
}
