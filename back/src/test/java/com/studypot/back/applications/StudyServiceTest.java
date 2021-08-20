package com.studypot.back.applications;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.MeetingType;
import com.studypot.back.domain.StudyStatus;
import com.studypot.back.domain.User;
import com.studypot.back.domain.UserRepository;
import com.studypot.back.domain.query.QueryStudyRepository;
import com.studypot.back.domain.study.Study;
import com.studypot.back.domain.study.StudyCategory;
import com.studypot.back.domain.study.StudyCategoryRepository;
import com.studypot.back.domain.study.StudyJoinWaitingQueueRepository;
import com.studypot.back.domain.study.StudyLikeRepository;
import com.studypot.back.domain.study.StudyMember;
import com.studypot.back.domain.study.StudyMemberRepository;
import com.studypot.back.domain.study.StudyRepository;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.dto.study.StudyDetailResponseDto;
import com.studypot.back.s3.S3Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class StudyServiceTest {

  private StudyService studyService;

  @Mock
  private StudyRepository studyRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private S3Service s3Service;

  @Mock
  private StudyCategoryRepository studyCategoryRepository;

  @Mock
  private StudyJoinWaitingQueueRepository studyJoinWaitingQueueRepository;

  @Mock
  private StudyMemberRepository studyMemberRepository;

  @Mock
  private StudyLikeRepository studyLikeRepository;

  @Mock
  private QueryStudyRepository queryStudyRepository;

  private Long userId;

  private User mockUser;

  private Study mockStudy;

  private List<StudyCategory> studyCategoryList = new ArrayList<>();

  private List<StudyMember> studyMemberList = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    openMocks(this);
    this.studyService = new StudyService(
        studyRepository,
        s3Service,
        userRepository,
        studyCategoryRepository,
        studyJoinWaitingQueueRepository,
        studyMemberRepository,
        studyLikeRepository,
        queryStudyRepository
    );
    this.userId = 1L;
    this.studyCategoryList.add(StudyCategory.builder().category(CategoryName.JOB_INTERVIEW).build());
    this.studyMemberList.add(StudyMember.builder().userId(101L).build());
    mockUser = User.builder().id(userId).build();
    mockStudy = Study.builder()
        .status(StudyStatus.OPEN)
        .categories(studyCategoryList)
        .content("test")
        .leader(mockUser)
        .meetingType(MeetingType.ONLINE)
        .members(studyMemberList)
        .studyLikes(new ArrayList<>())
        .build();
  }

  @Test
  @DisplayName("스터디_생성_결과_확인")
  public void addStudy() throws IOException {

    given(studyRepository.save(any(Study.class))).willReturn(mockStudy);
    given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));

    List<CategoryName> categories = new ArrayList<>();
    categories.add(CategoryName.JOB_INTERVIEW);
    StudyCreateRequestDto studyCreateRequestDto = new StudyCreateRequestDto();
    studyCreateRequestDto.setCategories(categories);

    Study study = studyService.addStudy(userId, studyCreateRequestDto);

    assertThat(study.getLeader().getId(), is(1L));
    assertThat(study.getStatus().getValue(), is("Open"));
  }

  @Test
  @DisplayName("스터디_상세_조회_확인")
  public void getStudy() {
    given(studyRepository.findById(any())).willReturn(Optional.ofNullable(mockStudy));
    given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));

    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudy(1L, 1L);

    assertThat(studyDetailResponseDto.getContent(), is("test"));
  }

  @Test
  @DisplayName("스터디_멤버_인원_확인")
  public void checkStudyMemberSize() {
    this.studyMemberList.add(StudyMember.builder().userId(201L).build());

    given(studyRepository.findById(any())).willReturn(Optional.ofNullable(mockStudy));
    given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));

    StudyDetailResponseDto studyDetailResponseDto = studyService.getStudy(1L, 1L);

    assertThat(studyDetailResponseDto.getParticipatingNumber(), is(2));
  }

//  @Test
//  @DisplayName("ResponseDto_응답_확인")
//  public void getStudyList() {
//    List<Study> studyList = new ArrayList<>();
//    studyList.add(mockStudy);
//    Page<Study> page = new PageImpl<>(studyList);
//
//    given(studyRepository.findAll(PageRequest.of(0, 12, Sort.by(Direction.DESC, "createdAt")))).willReturn(page);
//    given(studyRepository.getFirstBy()).willReturn(Optional.ofNullable(mockStudy));
//    given(userRepository.findById(mockStudy.getLeaderUserId())).willReturn(Optional.ofNullable(mockUser));
//
//    InfinityScrollResponseDto dto = studyService.getStudyList(new PageableRequestDto(), 1L);
//
//    assertThat(dto.getLastIdOfStudyList(), is(mockStudy.getId()));
//  }
}