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

  public Optional<UserDetails> authenticateUser(AppUser appUserToAuthenticate) {
    Optional<AppUser> appUserFromDb = userRepository
        .findByUsername(appUserToAuthenticate.getUsername());
    if (!appUserFromDb.isPresent()) {
      return Optional.empty();
    }

    AppUser userFromDb = appUserFromDb.get();

    if (!BCrypt.checkpw(appUserToAuthenticate.getPassword(), userFromDb.getPassword())) {
      return Optional.empty();
    }

    String token = tokenService.generateToken(userFromDb);

    UserDetails userDetails = new UserDetails(userFromDb.getId(), userFromDb.getUsername(), userFromDb.getFirstName(),
        userFromDb.getLastName(), token);
    return Optional.of(userDetails);
  }

  public AppUser registerUser(AppUser appUser) {
    String hashedPassword = getHashedPassowrd(appUser.getPassword());
    appUser.setPassword(hashedPassword);
    return userRepository.save(appUser);
  }

  private static String getHashedPassowrd(String passwordToHash) {
    return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
  }

  public boolean isUsernameAlreadyUsed(String username) {
    return userRepository.findByUsernameIgnoreCase(username).isPresent();
  }

}
