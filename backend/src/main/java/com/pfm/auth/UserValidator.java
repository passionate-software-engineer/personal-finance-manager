package com.pfm.auth;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserValidator {

  private UserService userService;

  public List<String> validateUser(User user) {
    List<String> validationResults = new ArrayList<>();

    if (userService.isUsernameAlreadyUsed(user.getUsername())) {
      validationResults.add("Username already used");
    }

    return validationResults;
  }


}
