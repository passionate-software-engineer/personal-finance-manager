package com.pfm.auth;

import static com.pfm.config.MessagesProvider.USERNAME_OR_PASSWORD_IS_INCORRECT;
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

public class AppUserControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldRegisterUser() throws Exception {
    //given
    AppUser appUser = userMarian();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByUsernameAlreadyExist() throws Exception {
    //given
    AppUser appUser = userMarian();

    callRestToRegisterUserAndReturnUserId(appUser);

    //then
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST))));
    ;
  }

  @Test
  public void shouldValidateUser() throws Exception {
    //given
    AppUser appUser = userMarian();

    callRestToRegisterUserAndReturnUserId(appUser);

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingUser() throws Exception {

    //given
    AppUser appUser = userMarian();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", is(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT))));
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserPassword() throws Exception {

    //given
    AppUser appUser = userMarian();

    callRestToRegisterUserAndReturnUserId(appUser);

    //when
    appUser.setPassword("Wrong password");

    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest());
  }
}
