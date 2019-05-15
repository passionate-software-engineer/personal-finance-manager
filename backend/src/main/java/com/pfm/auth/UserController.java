package com.pfm.auth;

import static com.pfm.config.MessagesProvider.USERNAME_OR_PASSWORD_IS_INCORRECT;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/users")
@CrossOrigin
@RestController
public class UserController { // TODO extract API interface

  private UserService userService;
  private UserValidator userValidator;
  private UserInitializationService userInitializationService;
  private TokenService tokenService;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate) {
    Optional<UserDetails> authResponse = userService.authenticateUser(userToAuthenticate);
/**
 *[LOGGING IN] returns response to Frontend   AuthenticationService.ts, method login
 *
 */
    return authResponse.<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() ->
            ResponseEntity.badRequest().body(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT)));

  }

  // TODO should enable user to log out / invalidate session

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    List<String> validationResult = userValidator.validateUser(user);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }

    long userId = userService.registerUser(user).getId();

    userInitializationService.initializeUser(user.getId());

    return ResponseEntity.ok(userId);
  }

  @RequestMapping(value = "/refresh", method = RequestMethod.POST)
  public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
    String newAccessToken= tokenService.generateAccessToken(refreshToken);
    return ResponseEntity.ok(refreshToken);
  }

}

