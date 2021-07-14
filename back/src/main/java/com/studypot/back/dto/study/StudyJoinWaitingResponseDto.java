package com.studypot.back.dto.study;

import java.util.List;
import lombok.Data;

@Data
public class StudyJoinWaitingResponseDto {

  List<WaitingUserDto> waitingUsers;

  public StudyJoinWaitingResponseDto(List<WaitingUserDto> waitingUsers) {
    this.waitingUsers = waitingUsers;
  }
}
