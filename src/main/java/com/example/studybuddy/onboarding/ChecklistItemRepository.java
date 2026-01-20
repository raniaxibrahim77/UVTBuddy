package com.example.studybuddy.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    List<ChecklistItem> findByLanguageOrderBySortOrderAsc(String language);

    boolean existsByLanguage(String language);
}
