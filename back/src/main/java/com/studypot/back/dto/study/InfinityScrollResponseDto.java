package com.studypot.back.dto.study;

import com.studypot.back.domain.study.Study;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfinityScrollResponseDto implements InfinityPage<StudyListEachResponseDto> {

  Long lastIdOfStudyList;

  List<StudyListEachResponseDto> contents;

  public InfinityScrollResponseDto(List<StudyListEachResponseDto> eachResponseDtoList, Study lastStudy) {
    this.lastIdOfStudyList = lastStudy.getId();
    this.contents = eachResponseDtoList;
  }

  @Override
  public boolean isLast() {

    return lastIdOfStudyList.equals(contents.get(contents.size() - 1).getId());
  }
}