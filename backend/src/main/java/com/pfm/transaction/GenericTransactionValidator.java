package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GenericTransactionValidator {

  private CategoryService categoryService;
  private AccountService accountService;

  public <T extends TransactionProperties> List<String> validate(T transaction, long userId) {
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
        } else if (accountService.accountDoesNotExistByIdAndUserId(entry.getAccountId(), userId)) {
          validationErrors.add(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), entry.getAccountId()));
        }

        if (entry.getPrice() == null) {
          validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
        }
      }
    }

    if (transaction.getDate() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_DATE));
    }

    return validationErrors;
  }

}
