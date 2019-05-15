package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Token {

  private long userId;
  private String accessToken;
  private ZonedDateTime accessTokenExpiryDate;
  private String refreshToken;
  private ZonedDateTime refreshTokenExpiryDate;
}
