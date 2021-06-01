package com.studypot.back.dto.study;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import com.studypot.back.domain.StudyStatus;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudyCreateRequestDto {

  private List<CategoryName> categories;

  private String locatedAt;

  private String title;

  private String content;

  private Integer maxNumber;

  private MeetingType meetingType;

  private StudyStatus status;

  private MultipartFile thumbnail;

}
