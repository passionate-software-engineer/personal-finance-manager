package com.pfm.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AuthResponse {

  private Long id;
  private String username;
  private String firstName;
  private String lastName;
  private String token;

  public AuthResponse(User user, String token) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.token = token;
  }

}
