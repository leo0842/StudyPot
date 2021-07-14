package com.studypot.back.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipateStatus {

  NOT_PARTICIPATED("미참여"),
  WAITING("대기"),
  PARTICIPATED("참여");

  private final String participateStatus;
}
