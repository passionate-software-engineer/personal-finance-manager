package com.pfm.auth;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

  @RequestMapping(value = "authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate) {
    Optional<AuthResponse> authResponse = userService.authenticateUser(userToAuthenticate);

    if (authResponse.isPresent()) {
      return ResponseEntity.ok(authResponse.get());
    }

    return ResponseEntity.badRequest().body("Username or password is incorrect");
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    userService.registerUser(user);
    return ResponseEntity.ok().build();
  }
}

