package com.studypot.back.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyLikeResponseDto {

  private Integer likeCount;

  private Boolean like;

}
