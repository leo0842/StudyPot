package com.studypot.back.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {

  List<StudyCategory> findAllByCategory(Pageable pageable, CategoryName categoryName);

  List<StudyCategory> findAllByCategoryAndStudyIdLessThan(Pageable pageable, CategoryName categoryName, Long id);

}
