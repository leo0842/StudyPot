package com.studypot.back.domain.query;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import com.studypot.back.domain.study.QStudy;
import com.studypot.back.domain.study.QStudyCategory;
import com.studypot.back.domain.study.Study;
import com.studypot.back.dto.study.PageableRequestDto;
import com.studypot.back.dto.study.StudyListEachResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryStudyRepository {

  private final JPAQueryFactory query;

  public List<StudyListEachResponseDto> getFilteredStudy(PageableRequestDto request, Pageable pageable) {

    QStudy study = QStudy.study;
    QStudyCategory category = QStudyCategory.studyCategory;

    List<Study> studyList = query
        .selectFrom(study)
        .where(
            eqMeetingType(study, request.getMeetingType()),
            eqCategory(study, request.getCategoryName(), category),
            ltLastId(study, request.getLastId()))
        .limit(pageable.getPageSize())
        .orderBy(study.createdAt.desc())
        .fetch();

    return studyList.stream().map(StudyListEachResponseDto::new).collect(Collectors.toList());
  }

  public Long getFirstId(PageableRequestDto request, Pageable pageable) {

    QStudy study = QStudy.study;
    QStudyCategory category = QStudyCategory.studyCategory;

    return query.select(study.id)
        .from(study)
        .where(
            eqMeetingType(study, request.getMeetingType()),
            eqCategory(study, request.getCategoryName(), category))
        .limit(1)
        .fetchOne();
  }

  private Predicate ltLastId(QStudy study, Long lastId) {

    if (lastId == null) {

      return null;
    }

    return study.id.lt(lastId);
  }

  private Predicate eqMeetingType(QStudy study, MeetingType meetingType) {

    if (meetingType == null) {

      return null;
    }
    return study.meetingType.eq(meetingType);
  }

  private Predicate eqCategory(QStudy study, CategoryName categoryName, QStudyCategory category) {

    if (categoryName == null) {

      return null;
    }

    return category.study.id.eq(study.id).and(category.category.eq(categoryName));
  }

}

