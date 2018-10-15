package com.pfm.auth;

import static com.pfm.config.MessagesProvider.EMPTY_FIRST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_LAST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_PASSWORD;
import static com.pfm.config.MessagesProvider.EMPTY_USERNAME;
import static com.pfm.config.MessagesProvider.PASSWORD_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.USERNAME_CONTAINS_WHITSPACE;
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
import java.util.Arrays;
import java.util.Collection;
import junitparams.Parameters;
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
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", is(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT))));
  }

  @Test
  public void shouldReturnErrorCausedByNullUserPasswordUsernameFirstNameLastName() throws Exception {

    //given
    AppUser appUser = AppUser.builder().build();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_USERNAME))))
        .andExpect(jsonPath("$[1]", is(getMessage(EMPTY_PASSWORD))))
        .andExpect(jsonPath("$[2]", is(getMessage(EMPTY_FIRST_NAME))))
        .andExpect(jsonPath("$[3]", is(getMessage(EMPTY_LAST_NAME))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyUserPasswordUsernameFirstNameLastName() throws Exception {

    //given
    AppUser appUser = AppUser.builder()
        .firstName("   ")
        .lastName("      ")
        .password("    ")
        .username("   ")
        .build();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[1]", is(getMessage(PASSWORD_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[2]", is(getMessage(EMPTY_FIRST_NAME))))
        .andExpect(jsonPath("$[3]", is(getMessage(EMPTY_LAST_NAME))));
  }

  @Test
  @Parameters(method = "usernameAndPasswordWithWhitespaces")
  public void shouldReturnErrorCausedByWhiteSpacesInUsernameAndPassword(String username, String password) throws Exception {

    //given
    AppUser appUser = AppUser.builder()
        .username(username)
        .password(password)
        .build();

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[1]", is(getMessage(PASSWORD_CONTAINS_WHITSPACE))));
  }

  private Collection<Object[]> usernameAndPasswordWithWhitespaces() {
    return Arrays.asList(new Object[][]{
        {" Marian", " 1232sbbb"},
        {"Mar ian", "1232 sbbb"},
        {" Mar ian ", " 1232 sbbb "},
        {"Marian ", "1232sbbb "}
    });
  }
}