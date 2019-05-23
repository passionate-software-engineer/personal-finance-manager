package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Token {

  private String value;
  private ZonedDateTime expiryDate;

}
