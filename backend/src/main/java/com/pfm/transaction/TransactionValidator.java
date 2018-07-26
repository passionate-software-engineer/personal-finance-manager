package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class TransactionValidator {

  public List<String> validate(Transaction transaction) {
    List<String> validationErrors = new ArrayList<>();
    if (transaction.getDescription() == null || transaction.getDescription().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
    }
    if (transaction.getCategory() == null || transaction.getCategory().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
    }
    if (transaction.getAccount() == null || transaction.getAccount().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT_NAME));
    }
    if (transaction.getPrice() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
    }
    return validationErrors;
  }
}
