package com.pfm.auth;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {

  private Map<String, Token> accessTokensStorage = new HashMap<>();
  private Map<String, Token> refreshTokenStorage = new HashMap<>();
  private Map<Long, Tokens> tokensByUserId = new HashMap<>();

  private TokenService tokenService = new TokenService(accessTokensStorage, refreshTokenStorage, tokensByUserId);

  @Test
  public void shouldThrowExceptionCausedByNullAccessTokenExpiryDate() {
    //given
    Token accessToken = new Token("accessToken", null);
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    Token refreshToken = new Token("refreshToken", ZonedDateTime.now().plusMinutes(10));
    refreshTokenStorage.put(refreshToken.getValue(), refreshToken);
    Tokens tokens = new Tokens(1L, accessToken, refreshToken);
    tokensByUserId.put(1L, tokens);
    tokenService = new TokenService(accessTokensStorage, refreshTokenStorage, tokensByUserId);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.validateAccessToken(accessToken.getValue()));

    //then
    assertThat(exception.getMessage(), is(equalTo("AccessToken expiry time does not exist")));
  }

  @Test
  public void shouldReturnFalseCausedByExpiredAccessToken() {
    //given
    Token token = new Token("accessToken", ZonedDateTime.now());
    accessTokensStorage.put(token.getValue(), token);
    tokenService = new TokenService(accessTokensStorage, refreshTokenStorage, tokensByUserId);

    //then
    assertFalse(tokenService.validateAccessToken(token.getValue()));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingToken() {
    //given
    String token = "Fake Tokens";
    accessTokensStorage.put(token, null);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnAccessToken("Not existing Tokens"));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided accessToken does not exist")));
  }

  @Test
  public void shouldThrowExceptionCausedByNullRefreshTokenExpiryDate() {
    //given
    Token accessToken = new Token("accessToken", ZonedDateTime.now().plusMinutes(10));
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    Token refreshToken = new Token("refreshToken", null);
    refreshTokenStorage.put(refreshToken.getValue(), refreshToken);
    Tokens tokens = new Tokens(1L, accessToken, refreshToken);
    tokensByUserId.put(1L, tokens);
    tokenService = new TokenService(accessTokensStorage, refreshTokenStorage, tokensByUserId);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.isRefreshTokenValid(refreshToken.getValue()));
    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken expiry time does not exist")));
  }

  @Test
  public void shouldReturnFalseCausedByExpiredRefreshToken() {
    //given
    Token token = new Token("accessToken", ZonedDateTime.now());
    refreshTokenStorage.put(token.getValue(), token);

    //then
    assertFalse(tokenService.isRefreshTokenValid(token.getValue()));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingRefreshToken() {
    //given
    String token = "Fake Tokens";
    this.refreshTokenStorage.put(token, null);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnRefreshToken("Not existing token"));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided refreshToken does not exist")));
  }

  @Test
  public void shouldThrowExceptionCausedByNullRefreshTokenWhileValidatingRefreshToken() {
    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.isRefreshTokenValid(null));

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken cannot be null")));
  }

}
