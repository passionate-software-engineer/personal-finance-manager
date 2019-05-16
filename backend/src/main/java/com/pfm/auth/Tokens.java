package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Tokens {

  private long userId;
  private String accessToken;
  private ZonedDateTime accessTokenExpiryDate;
  private String refreshToken;
  private ZonedDateTime refreshTokenExpiryDate;

  public Tokens(long userId, String accessToken, ZonedDateTime accessTokenExpiryDate) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.accessTokenExpiryDate = accessTokenExpiryDate;
    this.refreshToken="";
    this.refreshTokenExpiryDate=ZonedDateTime.now();
  }
}
