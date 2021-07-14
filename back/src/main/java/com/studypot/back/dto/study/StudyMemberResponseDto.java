package com.studypot.back.dto.study;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyMemberResponseDto {

  private Boolean isMember;

  private List<StudyMemberDto> studyMemberList;
}
