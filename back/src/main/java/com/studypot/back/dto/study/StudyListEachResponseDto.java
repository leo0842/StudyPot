package com.studypot.back.dto.study;

import com.studypot.back.domain.User;
import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyCategory;
import com.studypot.back.dto.CategoryResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Setter
  private StudyLikeResponseDto studyLike;

  public StudyListEachResponseDto(Study study, StudyLikeResponseDto studyLikeResponseDto) {
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
    this.leader = makeLeaderDto(study.getLeader());
    this.studyLike = studyLikeResponseDto;
  }

  public StudyListEachResponseDto(Study study) {
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
    this.leader = makeLeaderDto(study.getLeader());
  }

  public LeaderDto makeLeaderDto(User leader) {

    return new LeaderDto(leader.getImageUrl(), leader.getName());
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