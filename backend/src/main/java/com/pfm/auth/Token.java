package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {

  private String value;
  private ZonedDateTime expiryDate;

}
