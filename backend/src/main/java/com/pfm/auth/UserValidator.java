package com.pfm.auth;

import static com.pfm.config.MessagesProvider.EMPTY_FIRST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_LAST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_PASSWORD;
import static com.pfm.config.MessagesProvider.EMPTY_USERNAME;
import static com.pfm.config.MessagesProvider.PASSWORD_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.USERNAME_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class UserValidator {

  private UserService userService;

  public List<String> validateUser(AppUser appUser) {
    List<String> validationResults = new ArrayList<>();

    if (appUser.getUsername() == null) {
      validationResults.add(getMessage(EMPTY_USERNAME));
    }

    if (appUser.getUsername() != null && StringUtils.containsWhitespace(appUser.getUsername())) {
      validationResults.add(getMessage(USERNAME_CONTAINS_WHITSPACE));
    }

    if (appUser.getUsername() != null && userService.isUsernameAlreadyUsed(appUser.getUsername())) {
      validationResults.add(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST));
    }

    if (appUser.getPassword() == null) {
      validationResults.add(getMessage(EMPTY_PASSWORD));
    }

    if (appUser.getPassword() != null && StringUtils.containsWhitespace(appUser.getPassword())) {
      validationResults.add(getMessage(PASSWORD_CONTAINS_WHITSPACE));
    }

    if (appUser.getFirstName() == null || appUser.getFirstName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_FIRST_NAME));
    }

    if (appUser.getLastName() == null || appUser.getLastName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_LAST_NAME));
    }

    return validationResults;
  }

}
