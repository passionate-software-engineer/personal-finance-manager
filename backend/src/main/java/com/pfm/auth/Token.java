package com.pfm.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class Token {

  private String value;
  private ZonedDateTime expiryDate;

  @JsonCreator
  public Token(
      @JsonProperty("value") String value,
      @JsonProperty("expiryDate") ZonedDateTime expiryDate) {
    this.value = value;
    this.expiryDate = expiryDate;
  }

}
