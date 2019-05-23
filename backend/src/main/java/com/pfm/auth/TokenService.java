package com.pfm.auth;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

  private Map<String, Token> accessTokensStorage = new HashMap<>();
  private Map<String, Token> refreshTokenStorage = new HashMap<>();
  private Map<Long, Tokens> tokensByUserId = new HashMap<>();

  public TokenService(Map<String, Token> accessTokensStorage, Map<String, Token> refreshTokenStorage,
      Map<Long, Tokens> tokensByUserId) {
    this.accessTokensStorage = accessTokensStorage;
    this.refreshTokenStorage = refreshTokenStorage;
    this.tokensByUserId = tokensByUserId;
  }

  public Tokens generateTokens(User user) {

    UUID accessTokenUuid = UUID.randomUUID();
    UUID refreshTokenUuid = UUID.randomUUID();
    Token accessToken = new Token(accessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes));
    Token refreshToken = new Token(refreshTokenUuid.toString(), ZonedDateTime.now().plusMinutes(refreshTokenExpiryTimeInMinutes));
    Tokens tokens = new Tokens(user.getId(), accessToken, refreshToken);
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    refreshTokenStorage.put(refreshToken.getValue(), refreshToken);
    tokensByUserId.put(user.getId(), tokens);

    return tokens;
  }

  public boolean validateAccessToken(String token) {
    Token tokensFromDb = accessTokensStorage.get(token);
    if (tokensFromDb == null) {
      return false;
    }

    ZonedDateTime expiryDate = tokensFromDb.getExpiryDate();
    if (expiryDate == null) {
      accessTokensStorage.remove(token);
      throw new IllegalStateException("Tokens expiry time does not exist");
    }

    return expiryDate.isAfter(ZonedDateTime.now());
  }

  public long getUserIdBasedOnAccessToken(String accessToken) {
    Tokens token = tokensByUserId
        .values()
        .stream()
        .filter(tokens -> tokens.getAccessToken().getValue().equals(accessToken))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Provided accessToken does not exist"));

    return token.getUserId();
  }

  public Optional<Long> getUserIdBasedOnRefreshToken(String refreshToken) {
    Tokens token = tokensByUserId
        .values()
        .stream()
        .filter(tokens -> tokens.getRefreshToken().getValue().equals(refreshToken))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Provided refreshToken does not exist"));

    return Optional.of(token.getUserId());
  }

  public Token generateAccessToken(String refreshToken) {
    validateRefreshToken(refreshToken);

    //long userId = getUserIdBasedOnRefreshToken(refreshToken).get();
    UUID newAccessTokenUuid = UUID.randomUUID();
    Token newAccessToken = new Token(newAccessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes));

    accessTokensStorage.put(newAccessToken.getValue(), newAccessToken);
    refreshTokenStorage.put(refreshToken, refreshTokenStorage.get(refreshToken));
    return newAccessToken;
  }

  public boolean validateRefreshToken(String refreshToken) {
    if (refreshToken == null) {
      throw new IllegalStateException("RefreshToken cannot be null");
    }
    Token tokensFromDb = refreshTokenStorage.get(refreshToken);

    if (tokensFromDb == null) {
      return false;
    }
    ZonedDateTime expiryDate = tokensFromDb.getExpiryDate();
    if (expiryDate == null) {
      accessTokensStorage.remove(tokensFromDb.getValue());
      throw new IllegalStateException("RefreshToken expiry time does not exist");
    }
    return expiryDate.isAfter(ZonedDateTime.now());
  }

}
