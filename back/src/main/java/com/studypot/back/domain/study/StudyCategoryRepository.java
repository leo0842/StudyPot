package com.studypot.back.domain.study;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {

  List<StudyCategory> findAllByCategoryAndStudy_MeetingTypeLike(Pageable pageable, CategoryName categoryName, MeetingType meetingType);

  List<StudyCategory> findAllByCategoryAndStudyIdLessThan(Pageable pageable, CategoryName categoryName, Long id);

  Optional<StudyCategory> getFirstByCategory(CategoryName categoryName);
}
