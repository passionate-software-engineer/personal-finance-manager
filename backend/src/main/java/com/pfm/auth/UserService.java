package com.pfm.auth;

import java.io.UnsupportedEncodingException;
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

  private UserRespository userRespository;
  private TokenService tokenService;

  public Optional<AuthResponse> authenticateUser(AppUser appUserToAuthenticate) {
    AppUser appUserFromDb = userRespository
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
    return userRespository.save(appUser);
  }

  static String getSha512SecurePassword(String passwordToHash) {
    String salt = "salt";
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt.getBytes("UTF-8"));
      byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return generatedPassword;
  }

  public boolean isUsernameAlreadyUsed(String username) {
    return userRespository.numberOfUsersWithThisUsername(username) > 0;
  }

}
