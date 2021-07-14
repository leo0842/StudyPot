package com.studypot.back.dto.study;

import com.studypot.back.domain.Study;
import java.util.List;
import lombok.Data;

@Data
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