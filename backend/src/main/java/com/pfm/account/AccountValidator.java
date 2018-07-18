package com.pfm.account;

import com.pfm.Messages;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

  public List<String> validate(Account account) {
    List<String> validationErrors = new ArrayList<>();

    if (account.getName() == null || account.getName().equals("")) {
      validationErrors.add(Messages.EMPTY_ACCOUNT_NAME);
    }

    if (account.getBalance() == null) {
      validationErrors.add(Messages.EMPTY_ACCOUNT_BALANCE);
    }

    return validationErrors;
  }
}