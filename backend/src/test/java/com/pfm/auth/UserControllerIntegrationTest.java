package com.pfm.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import org.junit.Test;

public class UserControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldRegisterUser() throws Exception {
    //given
    User user = User.builder()
        .firstName("Sebastian")
        .lastName("Malik")
        .username("Seba")
        .password("123456")
        .build();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldValidateUser() throws Exception {
    //given
    User user = User.builder()
        .id(1L)
        .firstName("Sebastian")
        .lastName("Malik")
        .username("Seba")
        .password("123456")
        .build();

    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isOk());

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingUser() throws Exception {

    //given
    User user = User.builder()
        .firstName("Sebastian")
        .lastName("Malik")
        .username("Seba")
        .password("123456")
        .build();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isBadRequest());
  }
}
