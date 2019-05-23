package com.pfm.auth;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  @Value("${accessTokenExpiryTimeInMinutes}")
  private long accessTokenExpiryTimeInMinutes;

  @Value("${refreshTokenExpiryTimeInMinutes}")
  private long refreshTokenExpiryTimeInMinutes;

  private Map<String, Tokens> accessTokensStorage = new HashMap<>();
  private Map<String, Tokens> refreshTokenStorage = new HashMap<>();

  public TokenService(Map<String, Tokens> accessTokensStorage, Map<String, Tokens> refreshTokenStorage) {
    this.accessTokensStorage = accessTokensStorage;
    this.refreshTokenStorage = refreshTokenStorage;
  }

  public Tokens generateTokens(User user) {

    UUID accessTokenUuid = UUID.randomUUID();
    UUID refreshTokenUuid = UUID.randomUUID();
    Tokens tokens = new Tokens(user.getId(), accessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes),
        refreshTokenUuid.toString(),
        ZonedDateTime.now().plusMinutes(refreshTokenExpiryTimeInMinutes));
    accessTokensStorage.put(tokens.getAccessToken(), tokens);
    refreshTokenStorage.put(tokens.getRefreshToken(), tokens);

    return tokens;
  }

  public boolean validateAccessToken(String token) {
    Tokens tokensFromDb = accessTokensStorage.get(token);
    if (tokensFromDb == null) {
      return false;
    }

    ZonedDateTime expiryDate = tokensFromDb.getAccessTokenExpiryDate();
    if (expiryDate == null) {
      accessTokensStorage.remove(token);
      throw new IllegalStateException("Tokens expiry time does not exist");
    }

    return expiryDate.isAfter(ZonedDateTime.now());
  }

  public long getUserIdBasedOnAccessToken(String accessToken) {
    Tokens tokensFromDb = accessTokensStorage.get(accessToken);

    if (tokensFromDb == null) {
      throw new IllegalStateException("Provided accessToken does not exist");
    }

    return tokensFromDb.getUserId();
  }

  public long getUserIdBasedOnRefreshToken(String refreshToken) {
    Tokens tokensFromDb = refreshTokenStorage.get(refreshToken);

    if (tokensFromDb == null) {
      throw new IllegalStateException("Provided refreshToken does not exist");
    }

    return tokensFromDb.getUserId();
  }

  public Token generateAccessToken(String refreshToken) {
    validateRefreshToken(refreshToken);

    long userId = getUserIdBasedOnRefreshToken(refreshToken);
    Tokens tokens = refreshTokenStorage.get(refreshToken);
    UUID newAccessTokenUuid = UUID.randomUUID();
    Tokens tokensToUpdate = new Tokens(userId, newAccessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes),
        tokens.getRefreshToken(), tokens.getRefreshTokenExpiryDate());

    accessTokensStorage.put(tokensToUpdate.getAccessToken(), tokensToUpdate);
    refreshTokenStorage.put(tokensToUpdate.getRefreshToken(), tokensToUpdate);
    return new Token(tokensToUpdate.getAccessToken(), tokensToUpdate.getAccessTokenExpiryDate());
  }

  public boolean validateRefreshToken(String refreshToken) {
    if (refreshToken == null) {
      throw new IllegalStateException("RefreshToken cannot be null");
    }
    Tokens tokensFromDb = refreshTokenStorage.get(refreshToken);

    if (tokensFromDb == null) {
      return false;
    }
    ZonedDateTime expiryDate = tokensFromDb.getRefreshTokenExpiryDate();
    if (expiryDate == null) {
      accessTokensStorage.remove(tokensFromDb.getAccessToken());
      throw new IllegalStateException("RefreshToken expiry time does not exist");
    }
    return expiryDate.isAfter(ZonedDateTime.now());
  }

}
