package com.studypot.back.interfaces;

import com.studypot.back.applications.StudyService;
import com.studypot.back.auth.UserId;
import com.studypot.back.domain.CategoryName;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.dto.study.StudyDetailResponseDto;
import com.studypot.back.dto.study.StudySimpleResponseDto;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyController {

  private final StudyService studyService;

  @PostMapping("/study")
  @ResponseStatus(HttpStatus.CREATED)
  public void addStudy(
      @UserId Long userId,
      StudyCreateRequestDto studyCreateRequestDto
  ) throws IOException {

    studyService.addStudy(userId, studyCreateRequestDto);

  }

  @GetMapping("/study/{id}")
  public StudyDetailResponseDto studyDetail(@PathVariable("id") Long id) {
    return studyService.getStudy(id);
  }

  @GetMapping("/study")
  public List<StudySimpleResponseDto> studyList(@RequestParam(required = false) CategoryName category) {
    return studyService.getStudyList(category);
  }


}
