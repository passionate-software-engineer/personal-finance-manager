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

  // Is this correct or this field should be static? Its easier to test this way and Spring ensure that this class will be incjected as sinleton
  private HashMap<String, Token> tokens = new HashMap<>();

  public String generateToken(AppUser appUser) {

    UUID uuid = UUID.randomUUID();
    Token token = new Token(uuid.toString(), appUser.getId(), LocalDateTime.now());
    tokens.put(token.getToken(), token);
    return token.getToken();
  }

  public boolean validateToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      return false;
    }

    //should return false or throw exepction ?
    LocalDateTime creationTime = tokenFromDb.getCreationTime();
    if (creationTime == null) {
      throw new IllegalStateException("Token creation time does not exist");
    }

    if (!creationTime.plusMinutes(15).isAfter(LocalDateTime.now())) {
      return false;
    }

    return true;
  }

  public long getUserIdFromToken(String token) {
    Token tokenFromDb = tokens.get(token);

    if (tokenFromDb == null) {
      throw new IllegalStateException("Provided token does not exist");
    }

    return tokenFromDb.getUserId();
  }

}
