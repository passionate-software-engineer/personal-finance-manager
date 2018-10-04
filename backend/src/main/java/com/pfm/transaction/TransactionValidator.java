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
public class TransactionValidator {

  private CategoryService categoryService;
  private AccountService accountService;

  List<String> validate(TransactionRequest transactionRequest) {
    List<String> validationErrors = new ArrayList<>();

    if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
    }

    if (transactionRequest.getCategoryId() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
    } else if (!categoryService.idExist(transactionRequest.getCategoryId())) {
      validationErrors.add(getMessage(CATEGORY_ID_DOES_NOT_EXIST));
    }

    if (transactionRequest.getAccountPriceEntries() == null || transactionRequest.getAccountPriceEntries().size() == 0) {
      validationErrors.add(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED));
    } else {
      for (AccountPriceEntry entry : transactionRequest.getAccountPriceEntries()) {
        // TODO add information which entry is wrong
        if (entry.getAccountId() == null) {
          validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT));
        } else if (!accountService.idExist(entry.getAccountId())) {
          validationErrors.add(getMessage(ACCOUNT_ID_DOES_NOT_EXIST));
        }

        if (entry.getPrice() == null) {
          validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
        }
      }
    }

    if (transactionRequest.getDate() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_DATE));
    }

    return validationErrors;
  }
}

