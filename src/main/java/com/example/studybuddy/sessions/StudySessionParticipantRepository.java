package com.example.studybuddy.sessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface StudySessionParticipantRepository extends JpaRepository<StudySessionParticipant, Long> {

    long countBySessionIdAndStatus(Long sessionId, ParticipationStatus status);

    Optional<StudySessionParticipant> findBySessionIdAndUserId(Long sessionId, Long userId);

    List<StudySessionParticipant> findBySessionIdAndStatus(Long sessionId, ParticipationStatus status);

    @Modifying
    @Transactional
    void deleteBySessionId(Long sessionId);

    @Query("""
      select p.session.id, count(p)
      from StudySessionParticipant p
      where p.session.id in :sessionIds and p.status = com.example.studybuddy.sessions.ParticipationStatus.JOINED
      group by p.session.id
    """)
    List<Object[]> joinedCounts(@Param("sessionIds") Collection<Long> sessionIds);

    @Query("""
      select p.session.id
      from StudySessionParticipant p
      where p.user.id = :userId
        and p.session.id in :sessionIds
        and p.status = com.example.studybuddy.sessions.ParticipationStatus.JOINED
    """)
    List<Long> joinedSessionIdsForUser(@Param("userId") Long userId, @Param("sessionIds") Collection<Long> sessionIds);
}
