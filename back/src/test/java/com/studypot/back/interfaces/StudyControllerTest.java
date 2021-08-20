package com.studypot.back.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studypot.back.applications.StudyService;
import com.studypot.back.dto.study.StudyCreateRequestDto;
import com.studypot.back.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudyController.class)
@Import({JwtUtil.class})
class StudyControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JwtUtil jwtUtil;

  private String token;

  @MockBean
  private StudyService studyService;

  @BeforeEach
  public void setUp() {
    this.token = jwtUtil.createAccessToken(1L, "leo");
  }

  @Test
  @DisplayName("스터디_생성_컨트롤러_테스트")
  public void addStudy() throws Exception {

    mvc.perform(post("/study")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .content("[\"title\", \"FIRST\"]"))
        .andExpect(status().isCreated());

    verify(studyService).addStudy(any(Long.class), any(StudyCreateRequestDto.class));
  }

}