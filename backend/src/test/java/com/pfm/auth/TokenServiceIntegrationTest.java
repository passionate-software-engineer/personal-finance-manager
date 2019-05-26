package com.pfm.auth;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.helpers.IntegrationTestsBase;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TokenServiceIntegrationTest extends IntegrationTestsBase {

  @Autowired
  TokenService tokenService;

  @AfterEach
  public void setup() {
    tokenService.getAccessTokensStorage().clear();
    tokenService.getRefreshTokenStorage().clear();
    tokenService.getTokensByUserId().clear();
  }

  @Test
  void shouldReturn1ForOneLoggedInUser() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());

    assertEquals(1, tokenService.getAccessTokensStorage().size());
    assertEquals(1, tokenService.getRefreshTokenStorage().size());
    assertEquals(1, tokenService.getTokensByUserId().size());
  }

  @Test
  void shouldReturn2ForTwoLoggedInUsers() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
    userId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    token = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    assertEquals(2, tokenService.getAccessTokensStorage().size());
    assertEquals(2, tokenService.getRefreshTokenStorage().size());
    assertEquals(2, tokenService.getTokensByUserId().size());
  }

  @Test
  void shouldRemoveTokensForUserWhoTriesToExtendSessionWithExpiredRefreshToken() throws Exception {
    //given
    final long userMarianId = callRestToRegisterUserAndReturnUserId(userMarian());
    final Tokens marianTokens = callRestToAuthenticateUserAndReturnTokens(userMarian());
    final long userZdzislawId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    Tokens zdzislawTokens = callRestToAuthenticateUserAndReturnTokens(userZdzislaw());

    assertEquals(2, tokenService.getAccessTokensStorage().size());
    assertEquals(2, tokenService.getRefreshTokenStorage().size());
    assertEquals(2, tokenService.getTokensByUserId().size());
    makeRefreshTokenExpired(userMarianId);

    //when
    mockMvc.perform(post(USERS_SERVICE_PATH + "/refresh")
        .contentType(JSON_CONTENT_TYPE)
        .content(marianTokens.getRefreshToken().getValue()))
        .andExpect(status().isBadRequest());

    //then
    assertNull(tokenService.getTokensByUserId().get(userMarianId));
    assertNull(tokenService.getAccessTokensStorage().get(marianTokens.getAccessToken().getValue()));
    assertNull(tokenService.getRefreshTokenStorage().get(marianTokens.getRefreshToken().getValue()));
    assertEquals(1, tokenService.getAccessTokensStorage().size());
    assertEquals(1, tokenService.getRefreshTokenStorage().size());
    assertEquals(1, tokenService.getTokensByUserId().size());
  }

  private void makeRefreshTokenExpired(long userId) {
    Map<Long, Tokens> tokensByUserId = tokenService.getTokensByUserId();

    Token refreshToken = tokensByUserId.get(userId).getRefreshToken();
    Token updatedRefreshToken = new Token(refreshToken.getValue(), ZonedDateTime.now());
    Token accessToken = tokensByUserId.get(userId).getAccessToken();
    Map<String, Token> updatedRefreshTokenStorage = tokenService.getRefreshTokenStorage();

    updatedRefreshTokenStorage.replace(updatedRefreshToken.getValue(), updatedRefreshToken);
    tokenService.setRefreshTokenStorage(updatedRefreshTokenStorage);

    Tokens updatedTokens = new Tokens(userId, accessToken, updatedRefreshToken);

    Map<Long, Tokens> updatedTokensByUserId = tokenService.getTokensByUserId();
    updatedTokensByUserId.replace(userId, updatedTokens);
    tokenService.setTokensByUserId(updatedTokensByUserId);
  }

}

