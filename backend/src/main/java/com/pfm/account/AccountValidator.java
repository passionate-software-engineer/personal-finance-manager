package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.INVALID_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.INVALID_CONTROL_SUM_FOR_POLISH_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.filter.FilterService;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountValidator {

  public static final String POLISH_ACCOUNT_NUMBER_PATTERN = "\\d{26}";

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

    if (account.getBankAccountNumber() == null || account.getBankAccountNumber().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NUMBER));
    } else {
      Matcher matcher = Pattern.compile(POLISH_ACCOUNT_NUMBER_PATTERN).matcher(account.getBankAccountNumber().trim());
      if (matcher.matches()) {
        if (!isControlSumCorrectForPolishAccount(account.getBankAccountNumber())) {
          validationResults.add(getMessage(INVALID_CONTROL_SUM_FOR_POLISH_ACCOUNT_NUMBER));
        }
      } else {
        validationResults.add(getMessage(INVALID_ACCOUNT_NUMBER));
      }
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

  boolean isControlSumCorrectForPolishAccount(String accountNumber) {
    final int theLargestTwoDigitPrime = 97;
    final String countryCodeForPoland = "2521";
    final int validAccountResult = 61;

    final int[] weights = {1, 10, 3, 30, 9, 90, 27, 76, 81, 34, 49, 5, 50, 15, 53, 45, 62, 38, 89, 17, 73, 51, 25, 56, 75, 71, 31, 19, 93, 57};
    StringBuilder controlSumFactorBuilder = new StringBuilder(accountNumber);

    controlSumFactorBuilder
        .append(countryCodeForPoland)
        .append(controlSumFactorBuilder, 0, 2)
        .delete(0, 2);

    int controlSum = 0;
    for (int i = 0; i < 30; i++) {
      controlSum += controlSumFactorBuilder.charAt(29 - i) * weights[i];
    }
    int result = controlSum % theLargestTwoDigitPrime;
    return result == validAccountResult;
  }
}
