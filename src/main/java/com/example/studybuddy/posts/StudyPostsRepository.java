package com.example.studybuddy.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface StudyPostsRepository extends JpaRepository<StudyPosts, Long> {
    Page<StudyPosts> findByType(PostType type, Pageable pageable);

    Page<StudyPosts> findByLanguage(String language, Pageable pageable);

    Page<StudyPosts> findByTypeAndLanguage(
            PostType type,
            String language,
            Pageable pageable
    );
}


