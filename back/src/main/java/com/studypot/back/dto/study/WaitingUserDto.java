package com.studypot.back.dto.study;

import com.studypot.back.domain.StudyJoinWaitingQueue;
import com.studypot.back.domain.User;
import lombok.Data;

@Data
public class WaitingUserDto {

  private Long waitingId;

  private String userName;

  private String imageUrl;

  private String joinContent;

  public WaitingUserDto(User user, StudyJoinWaitingQueue studyJoinWaitingQueue) {
    this.waitingId = studyJoinWaitingQueue.getId();
    this.userName = user.getName();
    this.imageUrl = user.getImageUrl();
    this.joinContent = studyJoinWaitingQueue.getJoinContent();
  }
}
