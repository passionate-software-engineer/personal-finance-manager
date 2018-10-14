package com.pfm.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private TokenService tokenService;

  public Optional<AuthResponse> authenticateUser(AppUser appUserToAuthenticate) {
    AppUser appUserFromDb = userRepository
        .findByUsernameAndPassword(appUserToAuthenticate.getUsername(), getSha512SecurePassword(appUserToAuthenticate.getPassword()));
    if (appUserFromDb == null) {
      return Optional.empty();
    }

    String token = tokenService.generateToken(appUserFromDb);

    AuthResponse authResponse = new AuthResponse(appUserFromDb, token);
    return Optional.of(authResponse);
  }

  public AppUser registerUser(AppUser appUser) {
    String hashedPassword = getSha512SecurePassword(appUser.getPassword());
    appUser.setPassword(hashedPassword);
    return userRepository.save(appUser);
  }

  private static String getSha512SecurePassword(String passwordToHash) {
    String salt = "salt";
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt.getBytes(StandardCharsets.UTF_8));
      byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte abyte : bytes) {
        sb.append(Integer.toString((abyte & 0xff) + 0x100, 16).substring(1));
      }
      generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return generatedPassword;
  }

  public boolean isUsernameAlreadyUsed(String username) {
    return userRepository.numberOfUsersWithThisUsername(username) > 0;
  }

}
