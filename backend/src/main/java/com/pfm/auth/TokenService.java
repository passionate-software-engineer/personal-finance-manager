package com.pfm.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class TokenService {

  private HashMap<String, Token> tokens = new HashMap<>();

  public String generateToken(AppUser appUser) {

    UUID uuid = UUID.randomUUID();
    Token token = new Token(uuid.toString(), appUser.getId(), LocalDateTime.now().plusMinutes(15));
    tokens.put(token.getToken(), token);
    return token.getToken();
  }

  public boolean validateToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      return false;
    }

    LocalDateTime expiryDate = tokenFromDb.getExpiryDate();
    if (expiryDate == null) {
      throw new IllegalStateException("Token creation time does not exist");
    }

    return expiryDate.isAfter(LocalDateTime.now());
  }

  public long getUserIdFromToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      throw new IllegalStateException("Provided token does not exist");
    }

    return tokenFromDb.getUserId();
  }

}
