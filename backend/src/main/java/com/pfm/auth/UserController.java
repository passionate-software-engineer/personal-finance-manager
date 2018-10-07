package com.pfm.auth;

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
  public ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate) {
    Optional<AuthResponse> authResponse = userService.authenticateUser(userToAuthenticate);

    if (authResponse.isPresent()) {
      return ResponseEntity.ok(authResponse.get());
    }

    return ResponseEntity.badRequest().body("Username or password is incorrect");
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    List<String> validationResult = userValidator.validateUser(user);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    long userId = userService.registerUser(user).getId();
    return ResponseEntity.ok(userId);
  }
}

