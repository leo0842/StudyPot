package com.studypot.back.interfaces;

import com.studypot.back.applications.StudyService;
import com.studypot.back.auth.UserId;
import com.studypot.back.dto.study.ApproveRejectDto;
import com.studypot.back.dto.study.InfinityScrollResponseDto;
import com.studypot.back.dto.study.PageableRequestDto;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.dto.study.StudyDetailResponseDto;
import com.studypot.back.dto.study.StudyJoinRequestDto;
import com.studypot.back.dto.study.StudyJoinWaitingResponseDto;
import com.studypot.back.dto.study.StudyLikeResponseDto;
import com.studypot.back.dto.study.StudyMemberResponseDto;
import com.studypot.back.dto.study.UserParticipateStatusDto;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @GetMapping("/study/{studyId}")
  public StudyDetailResponseDto studyDetail(@PathVariable("studyId") Long studyId) {
    return studyService.getStudy(studyId);
  }

  @GetMapping("/study")
  public InfinityScrollResponseDto studyList(
      PageableRequestDto pageableRequestDto
  ) {
    return studyService.getStudyList(pageableRequestDto);
  }

  @PostMapping("/study/{studyId}")
  @ApiOperation("스터디 참여 신청")
  @ResponseStatus(HttpStatus.CREATED)
  public void joinStudy(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId,
      @RequestBody StudyJoinRequestDto studyJoinRequestDto
  ) {

    studyService.joinStudy(userId, studyId, studyJoinRequestDto);
  }

  @GetMapping("/study/{studyId}/settings/waiting")
  @ApiOperation("참여 신청한 유저 대기열 리스트 조회")
  public StudyJoinWaitingResponseDto getStudyWaitingList(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    return studyService.getStudyWaitingList(userId, studyId);
  }

  @PostMapping("/study/{studyId}/settings/waiting")
  @ApiOperation("참여 승인 또는 거절")
  public void chooseStudyJoin(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId,
      @RequestBody ApproveRejectDto approveRejectDto
  ) {

    studyService.chooseStudyJoin(userId, studyId, approveRejectDto);
  }

  @GetMapping("/study/{studyId}/members")
  public StudyMemberResponseDto getStudyMembers(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    return studyService.getStudyMembers(userId, studyId);

  }

  @DeleteMapping("/study/{studyId}")
  public void deleteStudy(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    studyService.deleteStudy(userId, studyId);
  }

  @PostMapping("/study/{studyId}/like")
  public void likeStudy(
      @Valid @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    studyService.likeStudy(userId, studyId);
  }

  @GetMapping("/study/{studyId}/like")
  public StudyLikeResponseDto getLikeInfo(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    return studyService.getLikeInfo(userId, studyId);
  }

  @GetMapping("/study/{studyId}/participate")
  public UserParticipateStatusDto getParticipateStatus(
      @UserId Long userId,
      @PathVariable("studyId") Long studyId
  ) {

    return studyService.getParticipateStatus(userId, studyId);
  }
}
