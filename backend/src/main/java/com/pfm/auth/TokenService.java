package com.pfm.auth;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
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

  @Getter()
  private Map<Long, Tokens> tokensByUserId = new HashMap<>();

  public TokenService(Map<String, Token> accessTokensStorage, Map<String, Token> refreshTokenStorage,
      Map<Long, Tokens> tokensByUserId) {
    this.accessTokensStorage = accessTokensStorage;
    this.refreshTokenStorage = refreshTokenStorage;
    this.tokensByUserId = tokensByUserId;
  }

  Tokens generateTokens(User user) {
    if (areLastSessionTokensExist(user.getId())) {
      removeAllUserTokens(user.getId());
    }
    UUID accessTokenUuid = UUID.randomUUID();
    UUID refreshTokenUuid = UUID.randomUUID();
    Token accessToken = new Token(accessTokenUuid.toString(), ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes), user.getId());
    Token refreshToken = new Token(refreshTokenUuid.toString(), ZonedDateTime.now().plusMinutes(refreshTokenExpiryTimeInMinutes), user.getId());
    final Tokens tokens = new Tokens(user.getId(), accessToken, refreshToken);
    saveTokens(user, tokens);

    return tokens;
  }

  public boolean validateAccessToken(String token) {
    Token tokensFromDb = accessTokensStorage.get(token);
    if (tokensFromDb == null) {
      return false;
    }

    ZonedDateTime expiryDate = tokensFromDb.getExpiryDate();
    if (expiryDate == null) {
      long userId = getUserIdBasedOnAccessToken(token);
      removeAllUserTokens(userId);
      throw new IllegalStateException("AccessToken expiry time does not exist");
    }

    return expiryDate.isAfter(ZonedDateTime.now());
  }

  public long getUserIdBasedOnAccessToken(String accessToken) {
    Token accessTokenFromDb = accessTokensStorage.get(accessToken);
    if (accessTokenFromDb == null) {
      throw new IllegalStateException("Provided accessToken does not exist");
    }
    return accessTokenFromDb.getUserId();
  }

  public long getUserIdBasedOnRefreshToken(String refreshToken) {
    Token refreshTokenFromDb = refreshTokenStorage.get(refreshToken);
    if (refreshTokenFromDb == null) {
      throw new IllegalStateException("Provided refreshToken does not exist");
    }
    return refreshTokenFromDb.getUserId();
  }

  Token generateAccessToken(String refreshToken) {
    long userId = getUserIdBasedOnRefreshToken(refreshToken);
    UUID newAccessTokenUuid = UUID.randomUUID();
    Token newAccessToken = Token.builder()
        .userId(userId)
        .value(newAccessTokenUuid.toString())
        .expiryDate(ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes))
        .build();

    updateTokens(userId, newAccessToken, refreshToken);

    return newAccessToken;
  }

  public boolean isRefreshTokenValid(String refreshToken) {
    if (refreshToken == null) {
      throw new IllegalStateException("RefreshToken cannot be null");
    }
    Token tokensFromDb = refreshTokenStorage.get(refreshToken);

    if (tokensFromDb == null) {
      return false;
    }
    ZonedDateTime expiryDate = tokensFromDb.getExpiryDate();
    if (expiryDate == null) {
      long userId = getUserIdBasedOnRefreshToken(refreshToken);
      removeAllUserTokens(userId);

      throw new IllegalStateException("RefreshToken expiry time does not exist");
    }
    if (!expiryDate.isAfter(ZonedDateTime.now())) {
      long userId = getUserIdBasedOnRefreshToken(refreshToken);
      removeAllUserTokens(userId);
    }
    return expiryDate.isAfter(ZonedDateTime.now());
  }

  private void removeAllUserTokens(Long id) {
    Tokens tokensToBeRemoved = tokensByUserId.get(id);
    accessTokensStorage.remove(tokensToBeRemoved.getAccessToken().getValue());
    refreshTokenStorage.remove(tokensToBeRemoved.getRefreshToken().getValue());
    tokensByUserId.remove(id);
  }

  private void updateTokens(long userId, Token newAccessToken, String refreshTokenValue) {
    String expiringAccessToken = tokensByUserId.get(userId).getAccessToken().getValue();
    accessTokensStorage.remove(expiringAccessToken);
    accessTokensStorage.put(newAccessToken.getValue(), newAccessToken);
    Token refreshToken = refreshTokenStorage.get(refreshTokenValue);
    Tokens tokens = new Tokens(userId, newAccessToken, refreshToken);
    tokensByUserId.replace(userId, tokens);
  }

  private void saveTokens(User user, Tokens tokens) {
    Token accessToken = tokens.getAccessToken();
    Token refreshToken = tokens.getRefreshToken();
    accessTokensStorage.put(accessToken.getValue(), accessToken);
    refreshTokenStorage.put(refreshToken.getValue(), refreshToken);
    tokensByUserId.put(user.getId(), tokens);
  }

  private boolean areLastSessionTokensExist(Long id) {
    return tokensByUserId.containsKey(id);
  }
}
