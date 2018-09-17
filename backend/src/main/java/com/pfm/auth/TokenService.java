package com.pfm.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {

  private static HashMap<String, Token> tokens = new HashMap<>();

  public String generateToken(User user) {

    UUID uuid = UUID.randomUUID();
    Token token = new Token(uuid.toString(), user.getId(), LocalDateTime.now());
    tokens.put(token.getToken(), token);
    return token.getToken();
  }

  public boolean validateToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      return false;
    }

    LocalDateTime creationTime = tokenFromDb.getCreationTime();
    if (creationTime == null) {
      throw new IllegalStateException();
    }

    if (!creationTime.plusMinutes(15).isAfter(LocalDateTime.now())) {
      return false;
    }

    return true;
  }

  public long getUserIdFromToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      throw new IllegalStateException();
    }

    return tokenFromDb.getUserId();
  }

}
