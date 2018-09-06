package com.pfm.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor
@RequestMapping("/users")
@CrossOrigin
@RestController
public class UserController {

  private List<User> usersDatabase = new ArrayList<>();

  @RequestMapping(value = "authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate) {
    Optional<User> userFromDb = usersDatabase.stream()
        .filter(user -> user.getUsername().equals(userToAuthenticate.getUsername()))
        .filter(user -> user.getPassword().equals(userToAuthenticate.getPassword()))
        .findFirst();

    if (userFromDb.isPresent()) {
      return ResponseEntity.ok(userFromDb.get());
    }

    return ResponseEntity.badRequest().body("Username or password is incorrect");
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    user.setToken("fake-jwt-token");

    usersDatabase.add(user);

    return ResponseEntity.ok(user);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static final class User {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String token;

  }

}