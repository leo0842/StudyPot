package com.studypot.back.applications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyCategory;
import com.studypot.back.domain.StudyCategoryRepository;
import com.studypot.back.domain.StudyMember;
import com.studypot.back.domain.StudyMemberRepository;
import com.studypot.back.domain.StudyRepository;
import com.studypot.back.domain.User;
import com.studypot.back.domain.UserRepository;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.dto.study.StudyDetailResponseDto;
import com.studypot.back.s3.S3Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class StudyServiceTest {

  private StudyService studyService;

  @Mock
  private StudyRepository studyRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private StudyMemberRepository studyMemberRepository;

  @Mock
  private StudyCategoryRepository studyCategoryRepository;

  @Mock
  private S3Service s3Service;

  @BeforeEach
  public void setUp() {
    openMocks(this);
    this.studyService = new StudyService(studyRepository, userRepository, studyMemberRepository, studyCategoryRepository, s3Service);
  }

  @Test
  public void addStudy() throws IOException {
    Long userId = 1L;
    User mockUser = User.builder().build();
    Study mockStudy = Study.builder().leaderUserId(userId).build();

    given(studyRepository.save(any(Study.class))).willReturn(mockStudy);
    given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(mockUser));

    List<CategoryName> categories = new ArrayList<>();
    categories.add(CategoryName.INTERVIEW);
    StudyCreateRequestDto studyCreateRequestDto = new StudyCreateRequestDto();
    studyCreateRequestDto.setCategories(categories);

    Study study = studyService.addStudy(userId, studyCreateRequestDto);

    assertThat(study.getLeaderUserId(), is(1L));
  }

  @Test
  public void getStudy() {
    Long studyId = 123L;
    Long leaderId = 1L;
    String userName = "leoleo";

    List<StudyCategory> categories = new ArrayList<>();
    categories.add(StudyCategory.builder().category(CategoryName.INTERVIEW).build());
    List<StudyMember> members = new ArrayList<>();
    members.add(new StudyMember());

    Study mockStudy = Study.builder().id(studyId).maxNumber(19).categories(categories).leaderUserId(leaderId).members(members).build();
    User mockUser = User.builder().id(leaderId).name(userName).build();

    given(studyRepository.findById(studyId)).willReturn(Optional.ofNullable(mockStudy));
    given(userRepository.findById(leaderId)).willReturn(Optional.ofNullable(mockUser));

    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudy(studyId);
    assertThat(studyDetailResponseDto.getMaxNumber(), is(19));
    assertThat(studyDetailResponseDto.getLeader().getName(), is("leoleo"));
    assertThat(studyDetailResponseDto.getParticipatingNumber(), is(1));
  }

}