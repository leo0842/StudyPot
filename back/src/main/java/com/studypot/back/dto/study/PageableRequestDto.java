package com.studypot.back.dto.study;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import lombok.Data;

@Data
public class PageableRequestDto {

  private CategoryName categoryName;

  private MeetingType meetingType;

  private Long lastId;

  private Integer size = 12;

  public boolean isEntireCategory() {

    return this.categoryName == null;
  }

  public boolean isFirst() {

    return this.lastId == null;
  }

}
