package com.pfm.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tokens {

  private long userId;
  private Token accessToken;
  private Token refreshToken;

}
