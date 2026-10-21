package com.enncompany.ecom.userinfo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enncompany.ecom.entity.UserInfo;
import com.enncompany.ecom.enums.UserType;
import com.enncompany.ecom.userinfo.repository.UserInfoRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private UserInfoRepository repository;
  @MockitoBean private PasswordEncoder passwordEncoder;

  private static final String BODY = "{\"loginId\":\"bob\",\"password\":\"secret\"}";

  @Test
  void verifyReturnsUserWhenPasswordMatches() throws Exception {
    UserInfo user = new UserInfo();
    user.setId(7L);
    user.setLoginId("bob");
    user.setUserType(UserType.CUSTOMER);
    user.setUserPass("hash");
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of(user));
    when(passwordEncoder.matches("secret", "hash")).thenReturn(true);

    mockMvc
        .perform(post("/internal/auth/verify").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(7))
        .andExpect(jsonPath("$.userType").value(2));
  }

  @Test
  void verifyReturns401WhenPasswordDoesNotMatch() throws Exception {
    UserInfo user = new UserInfo();
    user.setUserPass("hash");
    when(repository.findByLoginIdAndActiveTrue("bob")).thenReturn(List.of(user));
    when(passwordEncoder.matches("secret", "hash")).thenReturn(false);

    mockMvc
        .perform(post("/internal/auth/verify").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isUnauthorized());
  }
}
