package com.pfm.auth;

import static com.pfm.config.MessagesProvider.INVALID_REFRESH_TOKEN;
import static com.pfm.config.MessagesProvider.USERNAME_OR_PASSWORD_IS_INCORRECT;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.type.UserApi;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/users")
@CrossOrigin
@RestController
public class UserController implements UserApi {

  private UserService userService;
  private UserValidator userValidator;
  private UserInitializationService userInitializationService;
  private TokenService tokenService;

  @Override
  public ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate) {
    Optional<UserDetails> authResponse = userService.authenticateUser(userToAuthenticate);

    return authResponse.<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() ->
            ResponseEntity.badRequest()
                .body(Collections.singletonList(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT))));
    // TODO should return 401 instead of 400
  }

  // TODO should enable user to log out / invalidate session

  @Override
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    List<String> validationResult = userValidator.validateUser(user);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }

    long userId = userService.registerUser(user).getId();

    userInitializationService.initializeUser(user.getId());

    return ResponseEntity.ok(userId);
  }

  @Override
  public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
    if (!tokenService.isRefreshTokenValid(refreshToken)) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonList(getMessage(INVALID_REFRESH_TOKEN)));
    }
    Token newAccessToken = tokenService.generateAccessToken(refreshToken);

    return ResponseEntity.ok(newAccessToken);
  }

}
