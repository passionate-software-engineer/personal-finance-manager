package com.pfm.auth;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Token {

  private String token;
  private long userId;
  private LocalDateTime expiryDate;

}
