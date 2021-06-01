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
import com.studypot.back.domain.StudyStatus;
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

  private Long userId;
  private Long studyId;
  private String userName;
  private User mockUser;
  private Study mockStudy;
  private CategoryName categoryName;
  private StudyStatus status;
  private List<StudyCategory> categories = new ArrayList<>();
  private List<StudyMember> members = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    openMocks(this);
    this.studyService = new StudyService(studyRepository, userRepository, studyMemberRepository, studyCategoryRepository, s3Service);
    this.userId = 1L;
    this.studyId = 123L;
    this.userName = "leoleo";
    this.categoryName = CategoryName.INTERVIEW;
    this.status = StudyStatus.OPEN;
    this.categories.add(StudyCategory.builder().study(mockStudy).category(categoryName).build());
    this.members.add(new StudyMember());

    this.mockUser = User.builder().id(userId).name(userName).build();
    this.mockStudy = Study.builder().id(studyId).status(status).maxNumber(19).categories(categories).leaderUserId(userId).members(members)
        .build();
  }

  @Test
  public void addStudy() throws IOException {
    given(studyRepository.save(any(Study.class))).willReturn(mockStudy);
    given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(mockUser));

    List<CategoryName> categoryNames = new ArrayList<>();
    categoryNames.add(categoryName);
    StudyCreateRequestDto studyCreateRequestDto = new StudyCreateRequestDto();
    studyCreateRequestDto.setCategories(categoryNames);

    Study study = studyService.addStudy(userId, studyCreateRequestDto);

    assertThat(study.getLeaderUserId(), is(1L));
    assertThat(study.getStatus().getValue(), is("Open"));
  }

  @Test
  public void getStudy() {

    given(studyRepository.findById(studyId)).willReturn(Optional.ofNullable(mockStudy));
    given(userRepository.findById(userId)).willReturn(Optional.ofNullable(mockUser));

    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudy(studyId);
    assertThat(studyDetailResponseDto.getMaxNumber(), is(19));
    assertThat(studyDetailResponseDto.getLeader().getName(), is("leoleo"));
    assertThat(studyDetailResponseDto.getParticipatingNumber(), is(1));
  }

}