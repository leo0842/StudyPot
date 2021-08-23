package com.studypot.back.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

  List<StudyMember> findByUserId(Long userId);

  List<StudyMember> findByStudyAndUserId(Study study, Long userId);


}
