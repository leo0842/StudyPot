package com.studypot.back.dto.study;

import com.studypot.back.domain.ParticipateStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserParticipateStatusDto {

  private ParticipateStatus participateStatus;
}
