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

  public AuthResponse(Userek userek, String token) {
    this.id = userek.getId();
    this.username = userek.getUsername();
    this.firstName = userek.getFirstName();
    this.lastName = userek.getLastName();
    this.token = token;
  }

}
