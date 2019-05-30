package com.pfm.auth;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Token {

  private String value;
  private ZonedDateTime expiryDate;
  //@JsonIgnore
  private Long userId;
}
