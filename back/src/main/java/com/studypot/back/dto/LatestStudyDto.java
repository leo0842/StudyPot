package com.studypot.back.dto;

import com.studypot.back.dto.study.StudyListEachResponseDto;
import java.util.List;
import lombok.Data;

@Data
public class LatestStudyDto {

  List<StudyListEachResponseDto> contents;

  public LatestStudyDto(List<StudyListEachResponseDto> contents) {
    this.contents = contents;
  }
}
