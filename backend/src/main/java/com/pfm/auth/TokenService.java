package com.pfm.auth;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class TokenService {

  private HashMap<String, Tokens> tokensStorage = new HashMap<>();

  /**
   * [LOGGING IN] possibly need method like generateTokens (both access and refresh) to return it to userService
   */
  public Tokens generateTokens(User user) {

    UUID accessTokenUuid = UUID.randomUUID();
    UUID refreshTokenUuid = UUID.randomUUID();
    Tokens tokens = new Tokens(user.getId(), accessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(15),refreshTokenUuid.toString(),ZonedDateTime.now().plusMinutes(60));
    tokensStorage.put(tokens.getAccessToken(), tokens);
    return tokens;
  }

  public boolean validateAccessToken(String token) {
    Tokens tokensFromDb = tokensStorage.get(token);

    if (tokensFromDb == null) {
      return false;
    }

    ZonedDateTime expiryDate = tokensFromDb.getAccessTokenExpiryDate();
    if (expiryDate == null) {
      tokensStorage.remove(token);
      throw new IllegalStateException("Tokens expiry time does not exist");
    }

    return expiryDate.isAfter(ZonedDateTime.now());
  }

  public long getUserIdBasedOnAccessToken(String token) {
    Tokens tokensFromDb = tokensStorage.get(token);

    if (tokensFromDb == null) {
      throw new IllegalStateException("Provided accessToken does not exist");
    }

    return tokensFromDb.getUserId();
  }

  public String generateAccessToken(String refreshToken) {
    return null;
  }
}
