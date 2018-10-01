package com.pfm.auth;

import static com.pfm.config.MessagesProvider.USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

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
      validationResults.add(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST));
    }

    return validationResults;
  }

}
