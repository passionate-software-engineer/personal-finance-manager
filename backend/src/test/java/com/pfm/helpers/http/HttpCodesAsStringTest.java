package com.pfm.helpers.http;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class HttpCodesAsStringTest {

  @Test
  void shouldReturnProperCodeAsString() {
    // given
    HttpCodesAsString httpCodes = new HttpCodesAsString();

    // then
    assertNotNull(httpCodes);
    assertThat(HttpCodesAsString.OK, equalTo("200"));
    assertThat(HttpCodesAsString.BAD_REQUEST, equalTo("400"));
    assertThat(HttpCodesAsString.UNAUTHORIZED, equalTo("401"));
    assertThat(HttpCodesAsString.NOT_FOUND, equalTo("404"));
  }
}
