package com.studypot.back.dto.profile;

import com.studypot.back.domain.study.Study;
import lombok.Data;

@Data
public class StudyListDto {

  private Long studyId;

  private String studyTitle;

  private String studyContent;

  public StudyListDto(Study study) {
    this.studyId = study.getId();
    this.studyTitle = study.getTitle();
    this.studyContent = study.getContent();
  }
}
