package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountValidator {

  private AccountService accountService;

  public List<String> validate(Account account) {
    List<String> validationResults = new ArrayList<>();

    if (account.getName() == null || account.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    if (account.getBalance() == null) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_BALANCE));
    }

    if (account.getName() != null && accountService.isAccountNameAlreadyUsed(account.getName())) {
      validationResults.add(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }

    return validationResults;
  }

}