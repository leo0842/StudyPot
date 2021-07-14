package com.studypot.back.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingType {

  ONLINE("온라인"),
  OFFLINE("오프라인"),
  ON_AND_OFFLINE("온/오프라인"),
  LINE("라인");

  private final String value;

}
