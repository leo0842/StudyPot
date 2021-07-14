package com.studypot.back.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyJoinWaitingQueueRepository extends JpaRepository<StudyJoinWaitingQueue, Long> {

  List<StudyJoinWaitingQueue> findByStudyId(Long studyId);

  StudyJoinWaitingQueue findByUserIdAndStudyIdAndActive(Long userId, Long studyId, boolean active);
}
