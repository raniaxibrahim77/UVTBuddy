package com.example.studybuddy.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChecklistProgressRepository extends JpaRepository<UserChecklistProgress, Long> {

    Optional<UserChecklistProgress> findByUserIdAndItemId(Long userId, Long itemId);

    @Query("""
        select p.item.id
        from UserChecklistProgress p
        where p.user.id = :userId
          and p.done = true
          and p.item.language = :language
    """)
    List<Long> findDoneItemIds(@Param("userId") Long userId,
                               @Param("language") String language);
}
