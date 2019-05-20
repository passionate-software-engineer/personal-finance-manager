package com.pfm.auth;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {

  private final HashMap<String, Tokens> tokens = new HashMap<>();
  private HashMap<String, Tokens> refreshTokenMap = new HashMap<>();

  private final TokenService tokenService = new TokenService(tokens, refreshTokenMap);

  @Test
  public void shouldThrowExceptionCausedByNullAccessTokenExpiryTime() {
    //given
    Tokens tokens = new Tokens(1L, "accessToken", null);
    this.tokens.put(tokens.getAccessToken(), tokens);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.validateAccessToken(tokens.getAccessToken()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Tokens expiry time does not exist")));
  }

  @Test
  public void shouldReturnFalseCausedByExpiredAccessToken() {
    //given
    Tokens tokens = new Tokens(1L, "Tokens", ZonedDateTime.now());
    this.tokens.put(tokens.getAccessToken(), tokens);

    //then
    assertFalse(tokenService.validateAccessToken(tokens.getAccessToken()));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingToken() {
    //given
    String token = "Fake Tokens";
    tokens.put(token, null);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnAccessToken("Not existing Tokens"));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided accessToken does not exist")));

  }

  @Test
  public void shouldThrowExceptionCausedByNullRefreshTokenExpiryTime() {
    //given
    Tokens tokens = new Tokens(1L, "accessToken", ZonedDateTime.now().plusMinutes(15), "refreshToken", null);
    this.refreshTokenMap.put("refreshToken", tokens);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.validateRefreshToken(tokens.getRefreshToken()));

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken expiry time does not exist")));
  }

  @Test
  public void shouldReturnFalseCausedByExpiredRefreshToken() {
    //given
    Tokens tokens = new Tokens(1L, "accessToken", ZonedDateTime.now().plusMinutes(15), "refreshToken", ZonedDateTime.now());
    this.refreshTokenMap.put("refreshToken", tokens);

    //then
    assertFalse(tokenService.validateRefreshToken(tokens.getRefreshToken()));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingRefreshToken() {
    //given
    String token = "Fake Tokens";
    this.refreshTokenMap.put(token, null);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnRefreshToken("Not existing token"));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided refreshToken does not exist")));
  }

 /* @Test
  public void shouldThrowExceptionCausedByNullRefreshTokenWhileGeneratingAccessToken() {
    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.generateAccessToken(null));

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken cannot be null")));
  }*/

  @Test
  public void shouldGenerateAccessToken() {
    //given
    String refreshToken = "23k4jh35ri3u232";

    //when
    Token accessToken = tokenService.generateAccessToken(refreshToken);

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken cannot be null")));
  }

  @Test
  public void shouldThrowExceptionCausedByNullRefreshTokenWhileValidatingRefreshToken() {
    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.validateRefreshToken(null));

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken cannot be null")));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingUserWithGivenRefreshTokenWhileGeneratingAccessToke() {
    //when
    String token = "refreshTokenOfNotExistingUser";
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.generateAccessToken(null));

    //then
    assertThat(exception.getMessage(), is(equalTo("RefreshToken cannot be null")));
  }

}
