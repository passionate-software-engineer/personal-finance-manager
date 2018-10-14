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

  public AuthResponse(AppUser appUser, String token) {
    this.id = appUser.getId();
    this.username = appUser.getUsername();
    this.firstName = appUser.getFirstName();
    this.lastName = appUser.getLastName();
    this.token = token;
  }

}
