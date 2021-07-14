package com.studypot.back.dto.study;

import lombok.Data;

@Data
public class ApproveRejectDto {

  private Long waitingId;

  private String choice;
}
