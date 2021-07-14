package com.studypot.back.interfaces;

import com.studypot.back.applications.StudyService;
import com.studypot.back.dto.LatestStudyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

  private final StudyService studyService;

  @GetMapping("/latest")
  public LatestStudyDto getLatestStudies() {

    return studyService.getLatestStudies();
  }
}
