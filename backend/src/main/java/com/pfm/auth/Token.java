package com.pfm.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Token {

  private String value;
  private ZonedDateTime expiryDate;
  @JsonIgnore
  @NonNull
  private Long userId;
}
