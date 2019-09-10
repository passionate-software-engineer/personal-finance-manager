package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_ARCHIVED;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.FUTURE_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.PAST_PLANNED_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionValidator {

  private CategoryService categoryService;
  private AccountService accountService;

  public List<String> validate(Transaction transaction, long userId) {
    List<String> validationErrors = new ArrayList<>();

    if (transaction.getDescription() == null || transaction.getDescription().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
    }

    if (transaction.getCategoryId() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
    } else if (!categoryService.categoryExistByIdAndUserId(transaction.getCategoryId(), userId)) {
      validationErrors.add(String.format(getMessage(CATEGORY_ID_DOES_NOT_EXIST), transaction.getCategoryId()));
    }

    if (transaction.getAccountPriceEntries() == null || transaction.getAccountPriceEntries().size() == 0) {
      validationErrors.add(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED));
    } else {
      for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
        if (entry.getAccountId() == null) {
          validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT));
        } else {
          Optional<Account> accountOptional = accountService.getAccountByIdAndUserId(entry.getAccountId(), userId);
          if (!accountOptional.isPresent()) {
            validationErrors.add(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), entry.getAccountId()));
          } else if (accountOptional.get().isArchived()) {
            validationErrors.add(getMessage(ACCOUNT_IS_ARCHIVED));
          }
        }
        if (entry.getPrice() == null) {
          validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
        }
      }
    }
    if (transaction.getDate() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_DATE));
    } else {
      final boolean plannedTransaction = transaction.isPlanned();
      final boolean notPlannedTransaction = !plannedTransaction;

      if (notPlannedTransaction && isFutureDate(transaction.getDate())) {
        validationErrors.add(getMessage(FUTURE_TRANSACTION_DATE));
      }
      if (plannedTransaction) {
        if (isPastDate(transaction.getDate())) {
          validationErrors.add(getMessage(PAST_PLANNED_TRANSACTION_DATE));
        }
//        if (containsArchivedAccount(transaction,userId)) {
//          validationErrors.add(getMessage(TRANSACTION_TO_COMMIT_CONTAINS_ARCHIVED_ACCOUNT));
//        }
      }
    }

    return validationErrors;

  }

  private boolean containsArchivedAccount(Transaction transaction, long userId) {
    return transaction.getAccountPriceEntries().stream()
        .map(accountPriceEntry -> accountService.getAccountByIdAndUserId(accountPriceEntry.getAccountId(), userId).get())
        .anyMatch(Account::isArchived);
  }

    private boolean isPastDate (LocalDate date){
      return date.isBefore(LocalDate.now());
    }

    private boolean isFutureDate (LocalDate date){
      return date.isAfter(LocalDate.now());
    }
  }
