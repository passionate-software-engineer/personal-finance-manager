package com.pfm.auth;

import static com.pfm.config.MessagesProvider.EMPTY_FIRST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_LAST_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_PASSWORD;
import static com.pfm.config.MessagesProvider.EMPTY_USERNAME;
import static com.pfm.config.MessagesProvider.PASSWORD_CONTAINS_WHITSPACE;
import static com.pfm.config.MessagesProvider.TOO_LONG_FIRST_NAME;
import static com.pfm.config.MessagesProvider.TOO_LONG_LAST_NAME;
import static com.pfm.config.MessagesProvider.TOO_LONG_PASSWORD;
import static com.pfm.config.MessagesProvider.TOO_LONG_USERNAME;
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

  static final int PASSWORD_MAX_LENGTH = 255;
  static final int FIRST_NAME_MAX_LENGTH = 255;
  static final int LAST_NAME_MAX_LENGTH = 255;
  static final int USERNAME_MAX_LENGTH = 255;

  private UserService userService;

  List<String> validateUser(User user) {
    List<String> validationResults = new ArrayList<>();

    if (user.getUsername() == null) {
      validationResults.add(getMessage(EMPTY_USERNAME));
    }

    if (user.getUsername() != null && StringUtils.containsWhitespace(user.getUsername())) {
      validationResults.add(getMessage(USERNAME_CONTAINS_WHITSPACE));
    }

    if (user.getUsername() != null && userService.isUsernameAlreadyUsed(user.getUsername())) {
      validationResults.add(getMessage(USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST));
    }

    if (user.getUsername() != null && user.getUsername().length() > USERNAME_MAX_LENGTH) {
      validationResults.add(String.format(getMessage(TOO_LONG_USERNAME), USERNAME_MAX_LENGTH));
    }

    if (user.getPassword() == null) {
      validationResults.add(getMessage(EMPTY_PASSWORD));
    }

    if (user.getPassword() != null && StringUtils.containsWhitespace(user.getPassword())) {
      validationResults.add(getMessage(PASSWORD_CONTAINS_WHITSPACE));
    }

    if (user.getPassword() != null && user.getPassword().length() > PASSWORD_MAX_LENGTH) {
      validationResults.add(String.format(getMessage(TOO_LONG_PASSWORD), PASSWORD_MAX_LENGTH));
    }

    if (user.getFirstName() == null || user.getFirstName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_FIRST_NAME));
    }

    if (user.getFirstName() != null && user.getFirstName().length() > FIRST_NAME_MAX_LENGTH) {
      validationResults.add(String.format(getMessage(TOO_LONG_FIRST_NAME), FIRST_NAME_MAX_LENGTH));
    }

    if (user.getLastName() == null || user.getLastName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_LAST_NAME));
    }

    if (user.getFirstName() != null && user.getLastName().length() > LAST_NAME_MAX_LENGTH) {
      validationResults.add(String.format(getMessage(TOO_LONG_LAST_NAME), LAST_NAME_MAX_LENGTH));
    }

    return validationResults;
  }

}
