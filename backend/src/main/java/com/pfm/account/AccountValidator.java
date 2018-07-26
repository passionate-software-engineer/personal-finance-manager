package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXIST;
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

  public List<String> validateAccountName(Account account) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, account);

    if (account.getName() != null && !account.getName().trim().equals("")
        && accountService.isAccountNameAlreadyUsed(account.getName())) {
      validationResults.add(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXIST));
    }
    return validationResults;
  }

  public List<String> validate(List<String> validationResults, Account account) {

    if (account.getName() == null || account.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    if (account.getBalance() == null) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_BALANCE));
    }
    return validationResults;
  }
}