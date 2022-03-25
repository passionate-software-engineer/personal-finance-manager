package com.pfm.auth;

import static com.pfm.config.MessagesProvider.EMPTY_FIRST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_LAST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_PASSWORD;
import static com.pfm.config.MessagesProvider.EMPTY_USERNAME;
import static com.pfm.config.MessagesProvider.INVALID_REFRESH_TOKEN;
import static com.pfm.config.MessagesProvider.PASSWORD_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.TOO_LONG_FIRST_NAME;
import static com.pfm.config.MessagesProvider.TOO_LONG_LAST_NAME;
import static com.pfm.config.MessagesProvider.TOO_LONG_PASSWORD;
import static com.pfm.config.MessagesProvider.TOO_LONG_USERNAME;
import static com.pfm.config.MessagesProvider.USERNAME_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.USERNAME_OR_PASSWORD_IS_INCORRECT;
import static com.pfm.config.MessagesProvider.USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.helpers.IntegrationTestsBase;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UserControllerIntegrationTest extends IntegrationTestsBase {

  @SuppressWarnings("unused")
  private static Collection<Object[]> usernameAndPasswordWithWhitespaces() {
    return Arrays.asList(new Object[][]{
        {" Marian", " 1232sbbb"},
        {"Mar ian", "1232 sbbb"},
        {" Mar ian ", " 1232 sbbb "},
        {"Marian ", "1232sbbb "}
    });
  }

  @Test
  public void shouldRegisterUser() throws Exception {
    // given
    User user = userMarian();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByUsernameAlreadyExist() throws Exception {
    // given
    User user = userMarian();

    callRestToRegisterUserAndReturnUserId(user);

    // then
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST))));
  }

  @Test
  public void shouldReturnErrorCausedByUsernameAlreadyExistDifferentLettersSize() throws Exception {
    // given
    User user = userMarian();

    callRestToRegisterUserAndReturnUserId(user);

    user.setUsername(user.getUsername().toUpperCase());

    // then
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST))));
  }

  @Test
  public void shouldValidateUser() throws Exception {
    // given
    User user = userMarian();
    callRestToRegisterUserAndReturnUserId(user);

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingUser() throws Exception {
    // given
    User user = userMarian();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT))));
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserPassword() throws Exception {
    // given
    User user = userMarian();
    callRestToRegisterUserAndReturnUserId(user);

    // when
    user.setPassword("Wrong password");

    mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT))));
  }

  @Test
  public void shouldReturnErrorCausedByNullUserPasswordUsernameFirstNameLastName() throws Exception {
    // given
    User user = User.builder().build();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_USERNAME))))
        .andExpect(jsonPath("$[1]", is(getMessage(EMPTY_PASSWORD))))
        .andExpect(jsonPath("$[2]", is(getMessage(EMPTY_FIRST_NAME))))
        .andExpect(jsonPath("$[3]", is(getMessage(EMPTY_LAST_NAME))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyUserPasswordUsernameFirstNameLastName() throws Exception {
    // given
    User user = User.builder()
        .firstName("   ")
        .lastName("      ")
        .password("    ")
        .username("   ")
        .build();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[1]", is(getMessage(PASSWORD_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[2]", is(getMessage(EMPTY_FIRST_NAME))))
        .andExpect(jsonPath("$[3]", is(getMessage(EMPTY_LAST_NAME))));
  }

  @Test
  public void shouldReturnErrorsCausedByTooLongUserPasswordUsernameFirstNameLastName() throws Exception {
    // given
    User user = User.builder()
        .firstName("A".repeat(UserValidator.FIRST_NAME_MAX_LENGTH + 1))
        .lastName("B".repeat(UserValidator.LAST_NAME_MAX_LENGTH + 1))
        .password("C".repeat(UserValidator.PASSWORD_MAX_LENGTH + 1))
        .username("D".repeat(UserValidator.USERNAME_MAX_LENGTH + 1))
        .build();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(TOO_LONG_USERNAME), UserValidator.USERNAME_MAX_LENGTH))))
        .andExpect(jsonPath("$[1]", is(String.format(getMessage(TOO_LONG_PASSWORD), UserValidator.PASSWORD_MAX_LENGTH))))
        .andExpect(jsonPath("$[2]", is(String.format(getMessage(TOO_LONG_FIRST_NAME), UserValidator.FIRST_NAME_MAX_LENGTH))))
        .andExpect(jsonPath("$[3]", is(String.format(getMessage(TOO_LONG_LAST_NAME), UserValidator.LAST_NAME_MAX_LENGTH))));
  }

  @Test
  public void shouldRegisterUserCorrectlyWithMaxAllowedPasswordUsernameFirstNameLastNameLength() throws Exception {
    // given
    User user = User.builder()
        .firstName("A".repeat(UserValidator.FIRST_NAME_MAX_LENGTH))
        .lastName("B".repeat(UserValidator.LAST_NAME_MAX_LENGTH))
        .password("C".repeat(UserValidator.PASSWORD_MAX_LENGTH))
        .username("D".repeat(UserValidator.USERNAME_MAX_LENGTH))
        .build();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("usernameAndPasswordWithWhitespaces")
  public void shouldReturnErrorCausedByWhiteSpacesInUsernameAndPassword(String username, String password) throws Exception {
    // given
    User user = User.builder()
        .username(username)
        .password(password)
        .build();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/register")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", is(getMessage(USERNAME_CONTAINS_WHITSPACE))))
        .andExpect(jsonPath("$[1]", is(getMessage(PASSWORD_CONTAINS_WHITSPACE))));
  }

  @Test
  public void shouldReturnBadRequestForNullRefreshTokenDuringRefreshRequest() throws Exception {
    // given
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/refresh")
            .contentType(JSON_CONTENT_TYPE)
            .content(json(null)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0]", is(getMessage(INVALID_REFRESH_TOKEN))));
  }

  @Test
  public void shouldReturnNewAccessTokenOnSuccessfulRefreshRequest() throws Exception {
    // given
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    Tokens tokens = callRestToAuthenticateUserAndReturnTokens(userMarian());
    String refreshToken = tokens.getRefreshToken().getValue();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/refresh")
            .contentType(JSON_CONTENT_TYPE)
            .content(refreshToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(not(nullValue()))))
        .andExpect(content().string(containsString("value")))
        .andExpect(content().string(containsString("expiryDate")));
  }

  @Test
  public void shouldReturnCorrectTokenAndRemoveOldOneFromTokensStore() throws Exception {
    // given
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    Tokens tokens = callRestToAuthenticateUserAndReturnTokens(userMarian());
    String refreshToken = tokens.getRefreshToken().getValue();

    // when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/refresh")
            .contentType(JSON_CONTENT_TYPE)
            .content(refreshToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(not(nullValue()))))
        .andExpect(content().string(containsString("value")))
        .andExpect(content().string(containsString("expiryDate")));
  }

}
