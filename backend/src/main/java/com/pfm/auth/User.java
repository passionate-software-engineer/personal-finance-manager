package com.pfm.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class User {

  private Long id;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String token;

}
