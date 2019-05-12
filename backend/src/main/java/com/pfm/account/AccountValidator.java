package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.filter.FilterService;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountValidator {

  private AccountService accountService;
  private TransactionService transactionService;
  private FilterService filterService;

  private List<String> validate(Account account) {
    List<String> validationResults = new ArrayList<>();

    if (account.getName() == null || account.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    if (account.getBalance() == null) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_BALANCE));
    }

    return validationResults;
  }

  public List<String> validateAccountIncludingNameDuplication(long userId, Account account) {
    List<String> validationResults = validate(account);

    checkForDuplicatedName(userId, validationResults, account);

    return validationResults;
  }

  public List<String> validateAccountForUpdate(long id, long userId, Account account) {
    Optional<Account> accountToUpdate = accountService.getAccountByIdAndUserId(id, userId);

    if (!accountToUpdate.isPresent()) {
      throw new IllegalStateException("Account with id: " + id + " does not exist in database");
    }

    // it's ok when we keep name in updated account, it's not duplicate
    if (accountToUpdate.get().getName().equals(account.getName())) {
      return validate(account);
    }

    // it's not ok if account is duplicating name of other account
    return validateAccountIncludingNameDuplication(userId, account);
  }

  private void checkForDuplicatedName(long userId, List<String> validationResults, Account account) {
    if (account.getName() != null && !account.getName().trim().equals("") && accountService.isAccountNameAlreadyUsed(userId, account.getName())) {
      validationResults.add(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }

  public List<String> validateAccountForDelete(long accountId) {
    List<String> validationErrors = new ArrayList<>();

    if (transactionService.transactionExistByAccountId(accountId)) {
      validationErrors.add(getMessage(ACCOUNT_IS_USED_IN_TRANSACTION));
    }

    if (filterService.filterExistByAccountId(accountId)) {
      validationErrors.add(getMessage(ACCOUNT_IS_USED_IN_FILTER));
    }

    return validationErrors;
  }
}
