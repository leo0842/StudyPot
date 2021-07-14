package com.studypot.back.applications;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.Study;
import com.studypot.back.domain.StudyLike;
import com.studypot.back.domain.StudyLikeRepository;
import com.studypot.back.domain.StudyMember;
import com.studypot.back.domain.StudyMemberRepository;
import com.studypot.back.domain.StudyRepository;
import com.studypot.back.domain.User;
import com.studypot.back.domain.UserCategory;
import com.studypot.back.domain.UserCategoryRepository;
import com.studypot.back.domain.UserRepository;
import com.studypot.back.dto.CategoryResponseDto;
import com.studypot.back.dto.profile.PasswordChangeDto;
import com.studypot.back.dto.profile.ProfileResponseDto;
import com.studypot.back.dto.profile.StudyListDto;
import com.studypot.back.dto.profile.UpdateProfileRequestDto;
import com.studypot.back.exceptions.StudyNotFoundException;
import com.studypot.back.exceptions.UserNotFoundException;
import com.studypot.back.exceptions.WrongPasswordException;
import com.studypot.back.s3.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

  private final UserRepository userRepository;
  private final UserCategoryRepository userCategoryRepository;
  private final S3Service s3Service;
  private final StudyMemberRepository studyMemberRepository;
  private final PasswordEncoder passwordEncoder;
  private final StudyLikeRepository studyLikeRepository;
  private final StudyRepository studyRepository;

  public ProfileResponseDto getProfile(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    List<StudyMember> studyMemberList = studyMemberRepository.findByUserId(userId);
    List<StudyLike> studyLikeList = studyLikeRepository.findByUserId(userId);
    return new ProfileResponseDto(
        user.getName(),
        user.getLocation(),
        userCategoryList(user.getCategories()),
        user.getIntroduction(),
        user.getImageUrl(),
        getParticipatingStudyList(studyMemberList),
        getInterestingStudyList(studyLikeList));
  }

  private List<StudyListDto> getInterestingStudyList(List<StudyLike> studyLikeList) {

    return studyLikeList.stream()
        .filter(studyLike -> studyLike.getLikes().equals(true))
        .map(this::getStudyFromStudyLike)
        .map(StudyListDto::new)
        .collect(Collectors.toList());
  }

  private Study getStudyFromStudyLike(StudyLike trueStudyLike) {

    return studyRepository.findById(trueStudyLike.getStudyId()).orElseThrow(StudyNotFoundException::new);
  }

  private List<StudyListDto> getParticipatingStudyList(List<StudyMember> studyMemberList) {

    return studyMemberList.stream().map(studyMember -> new StudyListDto(studyMember.getStudy())).collect(Collectors.toList());
  }

  public ProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto updateProfileRequestDto) throws IOException {
    //TODO: PATCH 요청에 맞게 들어오는 key값만 변경하도록 바꾸기
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    String fileUrl = saveImage(updateProfileRequestDto.getImage(), user);

    //TODO: 딜리트 말고 다른 방안 찾아보기
    userCategoryRepository.deleteAllByUserId(userId);

    return updateUser(user, updateProfileRequestDto, fileUrl);

  }

  private List<CategoryResponseDto> userCategoryList(List<UserCategory> categories) {
    return categories.stream()
        .map(category -> new CategoryResponseDto(category.getCategory()))
        .collect(Collectors.toList());
  }

  private String saveImage(MultipartFile imageFile, User user) throws IOException {
    String fileUrl = user.getImageUrl();
    if (imageFile != null) {
      return uploadToS3(imageFile);
    }
    return fileUrl;
  }

  private ProfileResponseDto updateUser(User user, UpdateProfileRequestDto updateProfileRequestDto, String fileUrl) {
    user.setName(updateProfileRequestDto.getName());
    user.setLocation(updateProfileRequestDto.getLocation());
    updateCategories(updateProfileRequestDto.getCategories(), user);
    user.setIntroduction(updateProfileRequestDto.getIntroduction());
    user.setImageUrl(fileUrl);
    userRepository.save(user);

    List<StudyMember> studyMemberList = studyMemberRepository.findByUserId(user.getId());
    List<StudyLike> studyLikeList = studyLikeRepository.findByUserId(user.getId());
    
    return new ProfileResponseDto(
        user.getName(),
        user.getLocation(),
        userCategoryList(user.getCategories()),
        user.getIntroduction(),
        user.getImageUrl(),
        getParticipatingStudyList(studyMemberList),
        getInterestingStudyList(studyLikeList));
  }

  private void updateCategories(List<CategoryName> categories, User user) {
    Set<UserCategory> userCategorySet = new HashSet<>();
    for (CategoryName categoryName : categories) {

      userCategorySet.add(
          UserCategory.builder()
              .user(user)
              .category(categoryName)
              .build());
    }
    userCategoryRepository.saveAll(userCategorySet);
  }

  private String uploadToS3(MultipartFile multipartFile) throws IOException {
    String fileName = createFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());

    InputStream inputStream = multipartFile.getInputStream();

    return s3Service.uploadFile(fileName, inputStream, objectMetadata);
  }

  private String createFileName(String name) {
    return UUID.randomUUID().toString().concat(fileNameExtension(name));
  }

  private String fileNameExtension(String name) {
    try {
      return name.substring(name.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(String.format("not supported file: %s", name));
    }
  }

  public void changePassword(Long userId, PasswordChangeDto passwordChangeDto) {

    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    if (!passwordEncoder.matches(passwordChangeDto.getOriginalPassword(), user.getPassword())) {

      throw new WrongPasswordException();
    }

    user.changeNewPassword(passwordEncoder.encode(passwordChangeDto.getChangedPassword()));
    userRepository.save(user);
  }
}