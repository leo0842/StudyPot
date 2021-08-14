package com.studypot.back.dto.study;

import com.studypot.back.domain.study.Study;
import com.studypot.back.domain.study.StudyCategory;
import com.studypot.back.domain.User;
import com.studypot.back.dto.CategoryResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyListEachResponseDto {

  private Long id;

  private String thumbnail;

  private List<CategoryResponseDto> categories;

  private String title;

  private String content;

  private String locatedAt;

  private LocalDateTime createdAt;

  private String meetingType;

  private Integer maxNumber;

  private Integer participatingNumber;

  private LeaderDto leader;

  private StudyLikeResponseDto studyLike;

  public StudyListEachResponseDto(Study study, User leader, StudyLikeResponseDto studyLikeResponseDto) {
    this.id = study.getId();
    this.thumbnail = study.getThumbnailUrl();
    this.categories = studyCategoryList(study.getCategories());
    this.title = study.getTitle();
    this.content = study.getContent();
    this.locatedAt = study.getLocatedAt();
    this.createdAt = study.getCreatedAt();
    this.meetingType = study.getMeetingType().getValue();
    this.maxNumber = study.getMaxStudyNumber();
    this.participatingNumber = countMember(study);
    this.leader = new LeaderDto(leader.getImageUrl(), leader.getName());
    this.studyLike = studyLikeResponseDto;
  }

  private List<CategoryResponseDto> studyCategoryList(List<StudyCategory> categories) {

    return categories.stream()
        .map(category -> new CategoryResponseDto(category.getCategory()))
        .collect(Collectors.toList());
  }

  private int countMember(Study study) {
    return study.getMembers().size();
  }
}