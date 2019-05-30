package com.pfm.auth;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
    Token accessToken = new Token("accessToken", null,1L);
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    Token refreshToken = new Token("refreshToken", ZonedDateTime.now().plusMinutes(10),1L);
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
    Token token = new Token("accessToken", ZonedDateTime.now(),1L);
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
    Token accessToken = new Token("accessToken", ZonedDateTime.now().plusMinutes(10),1L);
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    Token refreshToken = new Token("refreshToken", null,1L);
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
  public void shouldThrowExceptionCausedByExpiredRefreshToken() {
    //given
    Token token = new Token("accessToken", ZonedDateTime.now(),1L);
    refreshTokenStorage.put(token.getValue(), token);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnRefreshToken(token.getValue()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided refreshToken does not exist")));
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

  @Test
  public void shouldRemoveAllUserTokensForExpiredRefreshToken() {
    //given
    Token accessToken = new Token("accessToken", ZonedDateTime.now().plusMinutes(15),1L);
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    Token refreshToken = new Token("refreshToken", ZonedDateTime.now(),1L);
    refreshTokenStorage.put(refreshToken.getValue(), refreshToken);
    Tokens tokens = new Tokens(1L, accessToken, refreshToken);
    tokensByUserId.put(1L, tokens);
    assertThat(tokensByUserId.get(1L), is(equalTo(tokens)));
    assertThat(accessTokensStorage.get(accessToken.getValue()), is(equalTo(accessToken)));
    assertThat(refreshTokenStorage.get(refreshToken.getValue()), is(equalTo(refreshToken)));

    //when
    tokenService.isRefreshTokenValid(refreshToken.getValue());

    //then
    assertThat(tokensByUserId.get(1L), is(nullValue()));
    assertThat(accessTokensStorage.get(accessToken.getValue()), is(nullValue()));
    assertThat(refreshTokenStorage.get(refreshToken.getValue()), is(nullValue()));
  }

}
