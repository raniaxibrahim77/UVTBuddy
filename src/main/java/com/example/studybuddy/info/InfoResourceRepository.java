package com.example.studybuddy.info;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InfoResourceRepository extends JpaRepository<InfoResource, Long> {
    List<InfoResource> findByCategory(InfoCategory category);
    List<InfoResource> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content
    );
}
