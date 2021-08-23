package com.studypot.back.dto.study;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import com.studypot.back.domain.StudyStatus;
import com.studypot.back.domain.User;
import com.studypot.back.domain.Study;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudyCreateRequestDto {

  private List<CategoryName> categories;

  private String locatedAt;

  private String title;

  private String content;

  private Integer maxStudyNumber;

  private MeetingType meetingType;

  private StudyStatus status;

  private MultipartFile thumbnail;

  public Study buildStudy(User leader, String thumbnailUrl) {
    return Study.builder()
        .locatedAt(this.locatedAt)
        .title(this.title)
        .content(this.content)
        .maxStudyNumber(this.maxStudyNumber)
        .meetingType(this.meetingType)
        .status(this.status)
        .thumbnailUrl(thumbnailUrl)
        .leader(leader)
        .build();
  }
}
