package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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

    return validationResults;
  }

  public List<String> validateAccountIncludingNameDuplication(Account account) {
    List<String> validationResults = validate(account);

    checkForDuplicatedName(validationResults, account);

    return validationResults;
  }

  public List<String> validateAccountForUpdate(long id, Account account) {
    Optional<Account> accountToUpdate = accountService.getAccountById(id);

    if (!accountToUpdate.isPresent()) {
      throw new IllegalStateException("Account with id: " + id + " does not exist in database");
    }

    // it's ok when we keep name in updated account, it's not duplicate
    if (accountToUpdate.get().getName().equals(account.getName())) {
      return validate(account);
    }

    // it's not ok if account is duplicating name of other account
    return validateAccountIncludingNameDuplication(account);
  }

  private void checkForDuplicatedName(List<String> validationResults, Account account) {
    if (account.getName() != null && !account.getName().trim().equals("") && accountService.isAccountNameAlreadyUsed(account.getName())) {
      validationResults.add(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }
}