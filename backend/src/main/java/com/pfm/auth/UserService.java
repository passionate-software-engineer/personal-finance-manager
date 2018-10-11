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

  public Optional<AuthResponse> authenticateUser(Userek userekToAuthenticate) {
    Userek userekFromDb = userRespository
        .findByUsernameAndPassword(userekToAuthenticate.getUsername(), getSha512SecurePassword(userekToAuthenticate.getPassword()));
    if (userekFromDb == null) {
      return Optional.empty();
    }

    String token = tokenService.generateToken(userekFromDb);

    AuthResponse authResponse = new AuthResponse(userekFromDb, token);
    return Optional.of(authResponse);
  }

  public Userek registerUser(Userek userek) {
    String hashedPassword = getSha512SecurePassword(userek.getPassword());
    userek.setPassword(hashedPassword);
    return userRespository.save(userek);
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
