package com.studypot.back.dto.study;

import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyCategory;
import com.studypot.back.dto.CategoryResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudySimpleResponseDto {

  private String thumbnail;

  private List<CategoryResponseDto> categories;

  private String title;

  private String content;

  private String locatedAt;

  private String meetingType;

  private Integer maxNumber;

  private Integer participatingNumber;

  public StudySimpleResponseDto(Study study) {
    this.categories = studyCategoryList(study.getCategories());
    this.thumbnail = study.getThumbnail();
    this.title = study.getTitle();
    this.content = study.getContent();
    this.locatedAt = study.getLocatedAt();
    this.meetingType = study.getMeetingType().getValue();
    this.maxNumber = study.getMaxNumber();
    this.participatingNumber = countMember(study);

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
