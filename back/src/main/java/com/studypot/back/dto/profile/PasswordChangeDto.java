package com.studypot.back.dto.profile;

import lombok.Data;

@Data
public class PasswordChangeDto {

  private String originalPassword;

  private String changedPassword;

}
