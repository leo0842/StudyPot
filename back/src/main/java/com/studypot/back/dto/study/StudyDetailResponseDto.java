package com.studypot.back.dto.study;

import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyCategory;
import com.studypot.back.domain.User;
import com.studypot.back.dto.CategoryResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class StudyDetailResponseDto {

  private final String thumbnail;

  private final LocalDateTime createdAt;

  private final List<CategoryResponseDto> categories;

  private final Integer maxNumber;

  private final Integer participatingNumber;

  private final String title;

  private final String content;

  private final LeaderDto leader;

  public StudyDetailResponseDto(Study study, User leader) {
    this.thumbnail = study.getThumbnail();
    this.createdAt = study.getCreatedAt();
    this.categories = studyCategoryList(study.getCategories());
    this.maxNumber = study.getMaxNumber();
    this.participatingNumber = memberCount(study);
    this.title = study.getTitle();
    this.content = study.getContent();
    this.leader = getLeaderInformation(leader);
  }

  private List<CategoryResponseDto> studyCategoryList(List<StudyCategory> categories) {
    return categories.stream()
        .map(category -> new CategoryResponseDto(category.getCategory()))
        .collect(Collectors.toList());
  }

  private int memberCount(Study study) {
    return study.getMembers().size();
  }

  private LeaderDto getLeaderInformation(User leader) {
    return new LeaderDto(leader.getImage(), leader.getName());
  }
}
