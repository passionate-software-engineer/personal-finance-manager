package com.pfm.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {

  private static HashMap<String, LocalDateTime> tokens = new HashMap<>();

  public String generateToken() {
    UUID uuid = UUID.randomUUID();
    String token = uuid.toString();
    tokens.put(token, LocalDateTime.now());
    return token;
  }

  public boolean validateToken(String token) {
    LocalDateTime creationDate = tokens.get(token);

    if (creationDate == null) {
      return false;
    }

    if (!creationDate.plusMinutes(15).isAfter(LocalDateTime.now())) {
      return false;
    }

    return true;
  }

}
