package com.pfm.account;

import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  public List<String> validate(Account account) {
    List<String> validationErrors = new ArrayList<>();

    if (account.getName() == null || account.getName().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    if (account.getBalance() == null) {
      validationErrors.add(getMessage(EMPTY_ACCOUNT_BALANCE));
    }
    return validationErrors;
  }

}