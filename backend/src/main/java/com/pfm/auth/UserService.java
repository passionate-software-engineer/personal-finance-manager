package com.pfm.auth;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private TokenService tokenService;

  private static String hashPassword(String passwordToHash) {
    return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
  }

  public Optional<UserDetails> authenticateUser(User userToAuthenticate) {
    Optional<User> appUserFromDb = userRepository.findByUsernameIgnoreCase(userToAuthenticate.getUsername());
    if (!appUserFromDb.isPresent()) {
      return Optional.empty();
    }

    User userFromDb = appUserFromDb.get();

    if (!BCrypt.checkpw(userToAuthenticate.getPassword(), userFromDb.getPassword())) {
      return Optional.empty();
    }

    Tokens tokens = tokenService.generateTokens(userFromDb);

    UserDetails userDetails = UserDetails.builder()
        .id(userFromDb.getId())
        .username(userFromDb.getUsername())
        .firstName(userFromDb.getFirstName())
        .lastName(userFromDb.getLastName())
        .accessToken(tokens.getAccessToken())
        .refreshToken(tokens.getRefreshToken())
        .build();

    return Optional.of(userDetails);
  }

  public User registerUser(User user) {
    String hashedPassword = hashPassword(user.getPassword());
    user.setPassword(hashedPassword);

    // to ignore id passed in request
    user.setId(null);

    return userRepository.save(user);
  }

  public boolean isUsernameAlreadyUsed(String username) {
    return userRepository.findByUsernameIgnoreCase(username).isPresent();
  }

}
