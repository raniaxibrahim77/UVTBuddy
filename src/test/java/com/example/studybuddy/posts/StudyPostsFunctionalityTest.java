package com.example.studybuddy.posts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudyPostsFunctionalityTest {

    @Test
    void prePersist_setsDefaultsWhenNull() {
        StudyPosts p = new StudyPosts();
        p.setType(null);
        p.setCreatedAt(null);

        p.prePersist();

        assertEquals(PostType.NOTE, p.getType());
        assertNotNull(p.getCreatedAt());
    }
}
