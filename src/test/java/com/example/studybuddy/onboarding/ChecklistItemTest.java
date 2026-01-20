package com.example.studybuddy.onboarding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChecklistItemTest {

    @Test
    void noArgsConstructor_createsObject() {
        assertNotNull(new ChecklistItem());
    }

    @Test
    void allArgsConstructor_setsFieldsCorrectly() {
        ChecklistItem i = new ChecklistItem(1L, "Title", "Desc", 1, "en");
        assertEquals("Title", i.getTitle());
        assertEquals(1, i.getSortOrder());
        assertEquals("en", i.getLanguage());
    }
}
