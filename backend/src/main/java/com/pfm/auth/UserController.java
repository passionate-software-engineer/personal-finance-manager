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
public class UserController {

  private UserService userService;
  private UserValidator userValidator;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody AppUser appUserToAuthenticate) {
    Optional<AuthResponse> authResponse = userService.authenticateUser(appUserToAuthenticate);

    if (authResponse.isPresent()) {
      return ResponseEntity.ok(authResponse.get());
    }

    return ResponseEntity.badRequest().body(getMessage(USERNAME_OR_PASSWORD_IS_INCORRECT));
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody AppUser appUser) {
    List<String> validationResult = userValidator.validateUser(appUser);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    long userId = userService.registerUser(appUser).getId();
    return ResponseEntity.ok(userId);
  }
}

