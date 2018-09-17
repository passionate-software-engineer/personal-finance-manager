package com.pfm.auth;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Token {

  String token;
  long userId;
  LocalDateTime creationTime;

}
