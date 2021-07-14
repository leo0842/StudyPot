package com.studypot.back.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {

  Optional<StudyLike> findByStudyIdAndUserId(Long studyId, Long userId);

  Optional<StudyLike> findByStudyIdAndUserIdAndLikesTrue(Long studyId, Long userId);

  List<StudyLike> findByUserId(Long userId);

  Integer countByStudyIdAndLikesTrue(Long studyId);
}
