package com.studypot.back.dto.study;

import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyCategory;
import com.studypot.back.domain.StudyMember;
import com.studypot.back.domain.User;
import com.studypot.back.dto.CategoryResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class StudyDetailResponseDto {

  private final Long studyId;

  private final String thumbnailUrl;

  private final LocalDateTime createdAt;

  private final String locatedAt;

  private final List<CategoryResponseDto> categories;

  private final Integer maxStudyNumber;

  private final Integer participatingNumber;

  private final String title;

  private final String content;

  private final LeaderDto leader;

  private final Integer studyLikeCount;

  public StudyDetailResponseDto(Study study, User leader) {
    this.studyId = study.getId();
    this.thumbnailUrl = study.getThumbnailUrl();
    this.createdAt = study.getCreatedAt();
    this.locatedAt = study.getLocatedAt();
    this.categories = getCategoryNameList(study.getCategories());
    this.maxStudyNumber = study.getMaxStudyNumber();
    this.participatingNumber = countMember(study.getMembers());
    this.title = study.getTitle();
    this.content = study.getContent();
    this.leader = getLeaderInformation(leader);
    this.studyLikeCount = study.countStudyLike();
  }

  private List<CategoryResponseDto> getCategoryNameList(List<StudyCategory> categories) {
    return categories.stream()
        .map(category -> new CategoryResponseDto(category.getCategory()))
        .collect(Collectors.toList());
  }

  private int countMember(List<StudyMember> studyMembers) {
    return studyMembers.size();
  }

  private LeaderDto getLeaderInformation(User leader) {
    return new LeaderDto(leader.getImageUrl(), leader.getName());
  }
}