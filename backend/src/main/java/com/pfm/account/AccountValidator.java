package com.pfm.account;

import static com.pfm.config.ResourceBundleConfig.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.ResourceBundleConfig.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.ResourceBundleConfig.getMessage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
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