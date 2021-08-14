package com.studypot.back.domain.study;

import com.studypot.back.domain.study.Study;
import com.studypot.back.domain.study.StudyMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

  List<StudyMember> findByUserId(Long userId);

  List<StudyMember> findByStudyAndUserId(Study study, Long userId);


}
