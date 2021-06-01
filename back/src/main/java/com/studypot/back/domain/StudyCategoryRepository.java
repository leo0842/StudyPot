package com.studypot.back.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {

  List<StudyCategory> findAllByCategory(CategoryName categoryName);

}
