package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Token {

  private String token;
  private long userId;
  private ZonedDateTime expiryDate;

}
