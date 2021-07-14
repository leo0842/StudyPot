package com.studypot.back.applications;

import com.studypot.back.domain.UserCategory;
import com.studypot.back.domain.CategoryName;
import com.studypot.back.domain.UserCategoryRepository;
import com.studypot.back.domain.User;
import com.studypot.back.domain.UserRepository;
import com.studypot.back.dto.user.UserSignupRequestDto;
import com.studypot.back.exceptions.ExistEmailException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserCategoryRepository userCategoryRepository;


  public User registerUser(UserSignupRequestDto signupRequestDto) {

    checkEmailExists(signupRequestDto.getEmail());
    String encodedPassword = encodePassword(signupRequestDto.getPassword());

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

    String randomUrl = randomList.get(random.nextInt(10));

    User savedUser = userRepository.save(
        User.builder()
            .email(signupRequestDto.getEmail())
            .name(signupRequestDto.getName())
            .password(encodedPassword)
            .imageUrl(randomUrl)
            .build()
    );
    saveCategories(signupRequestDto.getCategories(), savedUser);

    return savedUser;
  }

  private void checkEmailExists(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new ExistEmailException(email);
    }
  }

  private String encodePassword(String password) {
    String encodedPassword = passwordEncoder.encode(password);
    return encodedPassword;
  }

  private void saveCategories(List<CategoryName> categories, User user) {
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

}