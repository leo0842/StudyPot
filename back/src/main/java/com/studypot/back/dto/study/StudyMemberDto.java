package com.studypot.back.dto.study;

import com.studypot.back.domain.StudyMember;
import com.studypot.back.domain.User;
import lombok.Data;

@Data
public class StudyMemberDto {

  private String imageUrl;

  private String userName;

  private String userJoinContent;

  public StudyMemberDto(User user, StudyMember studyMember) {
    this.imageUrl = user.getImageUrl();
    this.userName = user.getName();
    this.userJoinContent = studyMember.getJoinContent();
  }
}
