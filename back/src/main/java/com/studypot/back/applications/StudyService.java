package com.studypot.back.applications;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.studypot.back.domain.MeetingType;
import com.studypot.back.domain.ParticipateStatus;
import com.studypot.back.domain.User;
import com.studypot.back.domain.UserRepository;
import com.studypot.back.domain.query.QueryStudyRepository;
import com.studypot.back.domain.study.Study;
import com.studypot.back.domain.study.StudyCategory;
import com.studypot.back.domain.study.StudyCategoryRepository;
import com.studypot.back.domain.study.StudyJoinWaitingQueue;
import com.studypot.back.domain.study.StudyJoinWaitingQueueRepository;
import com.studypot.back.domain.study.StudyLike;
import com.studypot.back.domain.study.StudyLikeRepository;
import com.studypot.back.domain.study.StudyMember;
import com.studypot.back.domain.study.StudyMemberRepository;
import com.studypot.back.domain.study.StudyRepository;
import com.studypot.back.dto.LatestStudyDto;
import com.studypot.back.dto.study.ApproveRejectDto;
import com.studypot.back.dto.study.InfinityScrollResponseDto;
import com.studypot.back.dto.study.PageableRequestDto;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.dto.study.StudyDetailResponseDto;
import com.studypot.back.dto.study.StudyJoinRequestDto;
import com.studypot.back.dto.study.StudyJoinWaitingResponseDto;
import com.studypot.back.dto.study.StudyLikeResponseDto;
import com.studypot.back.dto.study.StudyListEachResponseDto;
import com.studypot.back.dto.study.StudyMemberDto;
import com.studypot.back.dto.study.StudyMemberResponseDto;
import com.studypot.back.dto.study.UserParticipateStatusDto;
import com.studypot.back.dto.study.WaitingUserDto;
import com.studypot.back.exceptions.StudyNotFoundException;
import com.studypot.back.exceptions.UserNotFoundException;
import com.studypot.back.exceptions.UserNotPermittedException;
import com.studypot.back.s3.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudyService {

  private final StudyRepository studyRepository;

  private final S3Service s3Service;

  private final UserRepository userRepository;

  private final StudyCategoryRepository studyCategoryRepository;

  private final StudyJoinWaitingQueueRepository studyJoinWaitingQueueRepository;

  private final StudyMemberRepository studyMemberRepository;

  private final StudyLikeRepository studyLikeRepository;

  private final QueryStudyRepository queryStudyRepository;

  public Study addStudy(Long userId, StudyCreateRequestDto studyCreateRequestDto) throws IOException {
    String thumbnailUrl = createImageUrlOrNull(studyCreateRequestDto.getThumbnail());

    User leader = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    Study study = studyCreateRequestDto.buildStudy(leader, thumbnailUrl);
    study.addToStudyMemberList(userId);
    study.createStudyCategoryList(studyCreateRequestDto.getCategories());

    return studyRepository.save(study);
  }

  public StudyDetailResponseDto getStudy(Long studyId, Long userId) {
    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
    User leader = study.getLeader();

    return new StudyDetailResponseDto(study, leader, makeStudyLikeResponseDto(studyId, userId));
  }

  public InfinityScrollResponseDto getStudyList(PageableRequestDto pageableRequestDto, Long userId) {

    PageRequest pageRequest = PageRequest.of(0, pageableRequestDto.getSize(), sortCreatedAt());

    List<StudyListEachResponseDto> filteredStudy = queryStudyRepository.getFilteredStudy(pageableRequestDto, pageRequest);
    Long lastId = queryStudyRepository.getFirstId(pageableRequestDto);

    filteredStudy.forEach(dto -> dto.setStudyLike(makeStudyLikeResponseDto(dto.getId(), userId)));

    return new InfinityScrollResponseDto(lastId, filteredStudy);

  }

  public InfinityScrollResponseDto getStudyList2(PageableRequestDto pageableRequestDto, Long userId) {
    PageRequest pageRequest = PageRequest.of(0, pageableRequestDto.getSize(), sortCreatedAt());

    List<Study> studyList;

    if (pageableRequestDto.isFirst()) {

      if (pageableRequestDto.isEntireCategory()) {

        Page<Study> studyPage = studyRepository.findAll(pageRequest);
        studyList = studyPage.stream().collect(Collectors.toList());
      } else {

        List<StudyCategory> studyCategoryList = studyCategoryRepository
            .findAllByCategoryAndStudy_MeetingTypeLike(pageRequest, pageableRequestDto.getCategoryName(), MeetingType.ONLINE);
        studyList = studyCategoryList.stream().map(StudyCategory::getStudy).collect(Collectors.toList());
      }
    } else {

      if (pageableRequestDto.isEntireCategory()) {

        studyList = studyRepository.findAllByIdLessThan(pageRequest, pageableRequestDto.getLastId());
      } else {

        List<StudyCategory> studyCategoryList = studyCategoryRepository
            .findAllByCategoryAndStudyIdLessThan(
                pageRequest, pageableRequestDto.getCategoryName(), pageableRequestDto.getLastId()
            );
        studyList = studyCategoryList.stream().map(StudyCategory::getStudy).collect(Collectors.toList());
      }
    }

    List<StudyListEachResponseDto> studyListEachResponseDtoList = studyList.stream()
        .map(eachStudy -> new StudyListEachResponseDto(eachStudy, makeStudyLikeResponseDto(eachStudy.getId(), userId)))
        .collect(Collectors.toList());

    if (pageableRequestDto.isEntireCategory()) {

      Study study = studyRepository.getFirstBy().orElseThrow(StudyNotFoundException::new);

      return new InfinityScrollResponseDto(studyListEachResponseDtoList, study);
    } else {

      StudyCategory lastStudyCategory = studyCategoryRepository.getFirstByCategory(pageableRequestDto.getCategoryName())
          .orElseThrow(StudyNotFoundException::new);

      return new InfinityScrollResponseDto(studyListEachResponseDtoList, lastStudyCategory.getStudy());
    }
  }

  private StudyLikeResponseDto makeStudyLikeResponseDto(Long studyId, Long userId) {

    Integer likeCount = studyLikeRepository.countByStudyIdAndLikesTrue(studyId);
    Optional<StudyLike> studyLike = studyLikeRepository.findByStudyIdAndUserIdAndLikesTrue(studyId, userId);

    return new StudyLikeResponseDto(likeCount, studyLike.isPresent());
  }

  private User getLeader(Long leaderUserId) {

    return userRepository.findById(leaderUserId).orElseThrow(UserNotFoundException::new);
  }

  public void joinStudy(Long userId, Long studyId, StudyJoinRequestDto studyJoinRequestDto) {

    studyJoinWaitingQueueRepository.save(
        StudyJoinWaitingQueue.builder()
            .userId(userId)
            .studyId(studyId)
            .joinContent(studyJoinRequestDto.getJoinContent())
            .active(true)
            .build()
    );
  }

  public StudyJoinWaitingResponseDto getStudyWaitingList(Long userId, Long studyId) {

    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

    checkWhetherLeaderOrNot(userId, study);

    List<StudyJoinWaitingQueue> studyJoinWaitingQueueList = studyJoinWaitingQueueRepository.findByStudyId(studyId);

    return new StudyJoinWaitingResponseDto(
        studyJoinWaitingQueueList.stream()
            .map(studyJoinWaitingQueue -> new WaitingUserDto(getUserFromWaitingQueue(studyJoinWaitingQueue), studyJoinWaitingQueue))
            .collect(Collectors.toList()));
  }

  public void chooseStudyJoin(Long userId, Long studyId, ApproveRejectDto approveRejectDto) {

    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

    checkWhetherLeaderOrNot(userId, study);

    approveOrRejectToStudy(approveRejectDto, study);
  }

  private User getUserFromWaitingQueue(StudyJoinWaitingQueue studyJoinWaitingQueue) {

    return userRepository.findById(studyJoinWaitingQueue.getUserId()).orElseThrow(UserNotFoundException::new);
  }

  private void checkWhetherLeaderOrNot(Long userId, Study study) {
    if (!userId.equals(study.getLeader().getId())) {

      throw new UserNotPermittedException();
    }
  }

  private String createImageUrlOrNull(MultipartFile thumbnail) throws IOException {
    if (thumbnail != null) {
      return uploadToS3(thumbnail);
    }
    List<String> randomList = new ArrayList<>();
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/algalFuel_20bf6b.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/beniukonBronze_fa8231.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/desire_eb3b5a.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/flirtatious_fed330.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/fusionRed_fc5c65.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/maximumBlueGreen_2bcbba.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/nycTaxi_f7b731.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/orangeHibiscus_fd9644.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/reptileGreen_26de81.png");
    randomList.add("https://study-pot.s3.ap-northeast-2.amazonaws.com/turquoiseTopaz_0fb9b1.png");

    Random random = new Random();

    return randomList.get(random.nextInt(10));
  }

  private String uploadToS3(MultipartFile multipartFile) throws IOException {
    String fileName = createUUIDFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    InputStream inputStream = multipartFile.getInputStream();

    return s3Service.uploadFile(fileName, inputStream, objectMetadata);
  }

  private String createUUIDFileName(String name) {
    return UUID.randomUUID().toString().concat(fileNameExtension(name));
  }

  private String fileNameExtension(String name) {
    try {
      return name.substring(name.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(String.format("not supported file: %s", name));
    }
  }

  private Sort sortCreatedAt() {

    return Sort.by(Direction.DESC, "createdAt");
  }

  private void approveOrRejectToStudy(ApproveRejectDto approveRejectDto, Study study) {

    //TODO: 익셉션 추가!
    StudyJoinWaitingQueue waiting = studyJoinWaitingQueueRepository.findById(approveRejectDto.getWaitingId())
        .orElseThrow(StudyNotFoundException::new);
    waiting.setActive(false);
    studyJoinWaitingQueueRepository.save(waiting);

    if ("승인".equals(approveRejectDto.getChoice())) {

      studyMemberRepository.save(StudyMember.builder().study(study).userId(waiting.getUserId()).joinContent(waiting.getJoinContent()).build());
    }
  }

  public StudyMemberResponseDto getStudyMembers(Long userId, Long studyId) {

    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

    List<StudyMember> studyMemberList = study.getMembers();
    List<Long> studyUserIdList = studyMemberList.stream().map(StudyMember::getUserId).collect(Collectors.toList());

    if (!studyUserIdList.contains(userId)) {
      //TODO: 예외 추가하기!
      return new StudyMemberResponseDto(false, null);
    }
    return new StudyMemberResponseDto(true, buildStudyMemberListDto(studyMemberList));
  }

  private List<StudyMemberDto> buildStudyMemberListDto(List<StudyMember> studyMemberList) {

    return studyMemberList.stream().map(studyMember -> new StudyMemberDto(getUser(studyMember), studyMember)).collect(Collectors.toList());
  }

  private User getUser(StudyMember studyMember) {

    return userRepository.findById(studyMember.getUserId()).orElseThrow(UserNotFoundException::new);
  }

  public void deleteStudy(Long userId, Long studyId) {

    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

    checkWhetherLeaderOrNot(userId, study);

    studyRepository.delete(study);
  }

  public void likeStudy(Long userId, Long studyId) {

    if (userId == null) {

      throw new UserNotFoundException();
    }
    Optional<StudyLike> studyLike = studyLikeRepository.findByStudyIdAndUserId(studyId, userId);

    if (studyLike.isEmpty()) {

      studyLikeRepository.save(StudyLike.builder().studyId(studyId).userId(userId).likes(true).build());
    } else {

      studyLike.get().likeToggle();
      studyLikeRepository.save(studyLike.get());
    }
  }

  public UserParticipateStatusDto getParticipateStatus(Long userId, Long studyId) {

    if (userId == null) {

      return new UserParticipateStatusDto(ParticipateStatus.NOT_PARTICIPATED);
    }

    Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
    List<StudyMember> studyMemberList = studyMemberRepository.findByStudyAndUserId(study, userId);

    if (studyMemberList.isEmpty()) {

      StudyJoinWaitingQueue queue = studyJoinWaitingQueueRepository.findByUserIdAndStudyIdAndActive(userId, studyId, true);

      if (queue != null) {

        return new UserParticipateStatusDto(ParticipateStatus.WAITING);
      }

      return new UserParticipateStatusDto(ParticipateStatus.NOT_PARTICIPATED);
    }

    return new UserParticipateStatusDto(ParticipateStatus.PARTICIPATED);
  }

  public LatestStudyDto getLatestStudies() {

    List<Study> studyList = studyRepository.findTop6ByOrderByCreatedAtDesc();

    return new LatestStudyDto(
        studyList.stream().map(study -> new StudyListEachResponseDto(study, null))
            .collect(Collectors.toList()));
  }

  public StudyLikeResponseDto getLikeInfo(Long userId, Long studyId) {

    Optional<StudyLike> studyLike = studyLikeRepository.findByStudyIdAndUserIdAndLikesTrue(studyId, userId);

    Integer studyLikeCount = studyLikeRepository.countByStudyIdAndLikesTrue(studyId);

    return new StudyLikeResponseDto(studyLikeCount, studyLike.isPresent());
  }
}
