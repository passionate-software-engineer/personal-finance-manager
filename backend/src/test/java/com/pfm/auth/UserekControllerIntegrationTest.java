package com.pfm.auth;

import static com.pfm.config.MessagesProvider.USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import org.junit.Test;

public class UserekControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldRegisterUser() throws Exception {
    //given
    Userek userek = userMarian();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userek)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByUsernameAlreadyExist() throws Exception {
    //given
    Userek userek = userMarian();

    callRestToRegisterUserAndReturnUserId(userek);

    //then
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userek)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST))));
    ;
  }

  @Test
  public void shouldValidateUser() throws Exception {
    //given
    Userek userek = userMarian();

    callRestToRegisterUserAndReturnUserId(userek);

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userek)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingUser() throws Exception {

    //given
    Userek userek = userMarian();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userek)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserPassword() throws Exception {

    //given
    Userek userek = userMarian();

    callRestToRegisterUserAndReturnUserId(userek);

    //when
    userek.setPassword("Wrong password");

    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(userek)))
        .andExpect(status().isBadRequest());
  }
}
