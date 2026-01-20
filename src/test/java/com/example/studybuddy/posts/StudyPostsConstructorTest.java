package com.example.studybuddy.posts;

import com.example.studybuddy.users.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StudyPostsConstructorTest {

    @Test
    void noArgsConstructor_createsObject() {
        StudyPosts p = new StudyPosts();
        assertNotNull(p);
    }

    @Test
    void builder_setsFieldsCorrectly() {
        User author = User.builder()
                .name("Test")
                .email("test@test.com")
                .passwordHash("hashed")
                .role(User.Role.STUDENT)
                .build();

        StudyPosts p = StudyPosts.builder()
                .id(1L)
                .title("Title")
                .description("Desc")
                .course("PTS")
                .major("CS")
                .tags(Set.of("tag1"))
                .author(author)
                .type(PostType.NOTE)
                .language("en")
                .build();

        assertEquals(1L, p.getId());
        assertEquals("Title", p.getTitle());
        assertEquals("Desc", p.getDescription());
        assertEquals("PTS", p.getCourse());
        assertEquals("CS", p.getMajor());
        assertEquals(Set.of("tag1"), p.getTags());
        assertSame(author, p.getAuthor());
        assertEquals(PostType.NOTE, p.getType());
        assertEquals("en", p.getLanguage());
    }
}
