package com.studypot.back.applications;

import com.amazonaws.services.s3.model.ObjectMetadata;
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
import com.studypot.back.exceptions.StudyNotFoundException;
import com.studypot.back.exceptions.UserNotFoundException;
import com.studypot.back.s3.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudyService {

  private final StudyRepository studyRepository;

  private final UserRepository userRepository;

  private final StudyMemberRepository studyMemberRepository;

  private final StudyCategoryRepository studyCategoryRepository;
  private final S3Service s3Service;


  public Study addStudy(Long userId, StudyCreateRequestDto studyCreateRequestDto) throws IOException {

    String thumbnailUrl;
    if (studyCreateRequestDto.getThumbnail() != null) {
      thumbnailUrl = uploadToS3(studyCreateRequestDto.getThumbnail());
    } else {
      thumbnailUrl = null;
    }
    Study savedStudy = studyRepository.save(
        Study.builder()
            .locatedAt(studyCreateRequestDto.getLocatedAt())
            .title(studyCreateRequestDto.getTitle())
            .content(studyCreateRequestDto.getContent())
            .maxNumber(studyCreateRequestDto.getMaxNumber())
            .thumbnail(thumbnailUrl)
            .leaderUserId(userId)
            .build()
    );

    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    studyMemberRepository.save(StudyMember.builder().study(savedStudy).user(user).build());
    updateCategories(studyCreateRequestDto.getCategories(), savedStudy);

    return savedStudy;

  }

  private void updateCategories(List<CategoryName> categories, Study study) {
    Set<StudyCategory> studyCategorySet = new HashSet<>();
    for (CategoryName categoryName : categories) {

      studyCategorySet.add(
          StudyCategory.builder()
              .study(study)
              .category(categoryName)
              .build());
    }
    studyCategoryRepository.saveAll(studyCategorySet);
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

  public StudyDetailResponseDto getStudy(Long id) {
    Study study = studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
    User leader = userRepository.findById(study.getLeaderUserId()).orElseThrow(UserNotFoundException::new);
    return new StudyDetailResponseDto(study, leader);
  }

}
