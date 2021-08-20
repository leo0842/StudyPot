package com.studypot.back.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = Shape.OBJECT)
public enum MeetingType {

  ONLINE("온라인"),
  OFFLINE("오프라인"),
  ON_AND_OFFLINE("온/오프라인"),
  LINE("라인");

  private final String value;

  private String getKey() {

    return name();
  }
}
