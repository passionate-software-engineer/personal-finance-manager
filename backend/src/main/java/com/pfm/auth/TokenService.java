package com.pfm.auth;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j

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
    final Tokens tokens = new Tokens(user.getId(), accessToken, refreshToken);
    if (tokensAlreadyExistForUser(user.getId())) {
      removeTokens(user.getId());
    }
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
      long userId = getUserIdBasedOnAccessToken(token);
      accessTokensStorage.remove(token);
      refreshTokenStorage.remove(token);
      tokensByUserId.remove(userId);
      throw new IllegalStateException("AccessToken expiry time does not exist");
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

  public long getUserIdBasedOnRefreshToken(String refreshToken) {
    Tokens token = tokensByUserId
        .values()
        .stream()
        .filter(tokens -> tokens.getRefreshToken().getValue().equals(refreshToken))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Provided refreshToken does not exist"));

    return token.getUserId();
  }

  public Token generateAccessToken(String refreshToken) {
    isRefreshTokenValid(refreshToken);

    long userId = getUserIdBasedOnRefreshToken(refreshToken);
    UUID newAccessTokenUuid = UUID.randomUUID();
    Token newAccessToken = Token.builder()
        .value(newAccessTokenUuid.toString())
        .expiryDate(ZonedDateTime.now().plusMinutes(accessTokenExpiryTimeInMinutes))
        .build();
    String expiringAccessToken = tokensByUserId.get(userId).getAccessToken().getValue();
    accessTokensStorage.remove(expiringAccessToken);
    accessTokensStorage.put(newAccessToken.getValue(), newAccessToken);
    Token refreshTok = refreshTokenStorage.get(refreshToken);
    Tokens tokens = new Tokens(userId, newAccessToken, refreshTok);
    tokensByUserId.replace(userId, tokens);

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
      accessTokensStorage.remove(tokensFromDb.getValue());
      refreshTokenStorage.remove(tokensFromDb.getValue());
      tokensByUserId.remove(userId);

      throw new IllegalStateException("RefreshToken expiry time does not exist");
    }
    return expiryDate.isAfter(ZonedDateTime.now());
  }

  private boolean tokensAlreadyExistForUser(Long id) {
    return tokensByUserId.containsKey(id);
  }

  private void removeTokens(Long id) {
    Tokens tokensToBeRemoved = tokensByUserId.get(id);
    accessTokensStorage.remove(tokensToBeRemoved.getAccessToken().getValue());
    refreshTokenStorage.remove(tokensToBeRemoved.getRefreshToken().getValue());
    tokensByUserId.remove(id);
  }
}
