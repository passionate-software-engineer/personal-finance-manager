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

  private final HashMap<String, Token> tokens = new HashMap<>();
  private final TokenService tokenService = new TokenService(tokens);

  @Test
  public void shouldThrowExceptionCausedByNullExpiryTime() {
    //given
    Token token = new Token("Token", 1L, null);
    tokens.put(token.getToken(), token);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.validateToken(token.getToken()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Token expiry time does not exist")));
  }

  @Test
  public void shouldReturnFalseCausedByExpiredToken() {
    //given
    Token token = new Token("Token", 1L, ZonedDateTime.now());
    tokens.put(token.getToken(), token);

    //then
    assertFalse(tokenService.validateToken(token.getToken()));
  }

  @Test
  public void shouldThrowExceptionCausedByNotExistingToken() {
    //given
    String token = "Fake Token";
    tokens.put(token, null);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> tokenService.getUserIdBasedOnToken("Not existing Token"));

    //then
    assertThat(exception.getMessage(), is(equalTo("Provided token does not exist")));

  }
}
