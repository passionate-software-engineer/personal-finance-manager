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
  private final TokenService tokenService = new TokenService(tokens);

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
  public void shouldReturnFalseCausedByExpiredToken() {
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
}
