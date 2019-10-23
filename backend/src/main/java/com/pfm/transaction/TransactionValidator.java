package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_ARCHIVED;
import static com.pfm.config.MessagesProvider.ACCOUNT_PRICE_ENTRY_SIZE_CHANGED;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.DATE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.FUTURE_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.PRICE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionValidator {

  private final DateHelper dateHelper = new DateHelper();
  private CategoryService categoryService;
  private AccountService accountService;

  public List<String> validate(Transaction transaction, long userId, @Nullable Transaction originalTransaction) {
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
      if (originalTransaction != null && accountPriceEntrySizeChanged(transaction, originalTransaction) && transactionContainsArchivedAccount(
          transaction, userId)) {
        validationErrors.add(getMessage(ACCOUNT_PRICE_ENTRY_SIZE_CHANGED));
      } else {
        for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
          if (entry.getAccountId() == null) {
            validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT));
          } else {
            Optional<Account> accountOptional = accountService.getAccountByIdAndUserId(entry.getAccountId(), userId);
            if (!accountOptional.isPresent()) {
              validationErrors.add(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), entry.getAccountId()));
            } else if (transactionContainsArchivedAccount(transaction, userId)) {
              if (originalTransaction != null) {
                if (wasPriceChanged(originalTransaction, transaction)) {
                  validationErrors.add(getMessage(PRICE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED));
                }
                if (wasAccountChanged(originalTransaction, transaction)) {
                  validationErrors.add(getMessage(ACCOUNT_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED));
                }
                if (wasDateChanged(originalTransaction, transaction)) {
                  validationErrors.add(getMessage(DATE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED));
                }
              } else {
                validationErrors.add(getMessage(ACCOUNT_IS_ARCHIVED));
              }
            }
          }
          if (entry.getPrice() == null) {
            validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
          }
        }
      }
    }
    if (transaction.getDate() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_DATE));
    } else {
      final boolean notPlannedTransaction = !transaction.isPlanned();

      if (notPlannedTransaction && dateHelper.isFutureDate(transaction.getDate())) {
        validationErrors.add(getMessage(FUTURE_TRANSACTION_DATE));
      }
    }
    return validationErrors;
  }

  private boolean accountPriceEntrySizeChanged(Transaction transaction, Transaction originalTransaction) {
    return !isAccountPriceEntrySizeEqual(transaction, originalTransaction);
  }

  private boolean isAccountPriceEntrySizeEqual(Transaction transaction, Transaction originalTransaction) {
    return transaction.getAccountPriceEntries().size() == originalTransaction.getAccountPriceEntries().size();

  }

  private boolean wasDateChanged(Transaction originalTransaction, Transaction transaction) {
    return !(transaction.getDate().equals(originalTransaction.getDate()));
  }

  private boolean transactionContainsArchivedAccount(Transaction transaction, long userId) {
    for (int i = 0; i < transaction.getAccountPriceEntries().size(); i++) {
      final Long accountId = transaction.getAccountPriceEntries().get(i).getAccountId();
      Optional<Account> account = accountService.getAccountByIdAndUserId(accountId, userId);
      if (account.get().isArchived()) {
        return true;
      }
    }
    return false;
  }

  private boolean wasAccountChanged(Transaction transaction, Transaction updatedTransaction) {
    for (int i = 0; i < updatedTransaction.getAccountPriceEntries().size(); i++) {
      if (!(updatedTransaction.getAccountPriceEntries().get(i).accountId.equals(transaction.getAccountPriceEntries().get(i).accountId))) {
        return true;
      }
    }
    return false;
  }

  private boolean wasPriceChanged(Transaction originalTransaction, Transaction updatedTransaction) {
    for (int i = 0; i < updatedTransaction.getAccountPriceEntries().size(); i++) {
      if (updatedTransaction.getAccountPriceEntries().get(i).price.compareTo(originalTransaction.getAccountPriceEntries().get(i).price) != 0) {
        return true;
      }
    }
    return false;
  }

}
