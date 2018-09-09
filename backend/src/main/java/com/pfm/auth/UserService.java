package com.pfm.auth;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private UserRespository userRespository;

  public Optional<AuthResponse> authenticateUser(User userToAuthenticate) {
    User userFromDb = userRespository.findByUsername(userToAuthenticate.getUsername());
    if (userFromDb == null) {
      return Optional.empty();
    }


    if (!userFromDb.getPassword().equals(userToAuthenticate.getPassword())) {
      return Optional.empty();
    }
    String token = "fake-jwt-token";

    AuthResponse authResponse = new AuthResponse(userFromDb, token);
    return Optional.of(authResponse);
  }

  public User registerUser(User user) {
    return userRespository.save(user);
  }

}
