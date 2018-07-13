package com.pfm.account;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountValidator {

  public List<String> validate(Account account) {
    List<String> validationErrors = new ArrayList<>();

    if (account.getName() == null || account.getName().equals("")) {
      validationErrors.add("Account name is empty");
    }

    if (account.getBalance() == null) {
      validationErrors.add("Account balance is empty");
    }

    return validationErrors;
  }

}
