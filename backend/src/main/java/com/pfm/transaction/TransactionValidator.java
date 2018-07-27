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

  public List<String> validate(Transaction transaction) { // TODO decide if you validate request or the created object
    List<String> validationErrors = new ArrayList<>();
    if (transaction.getDescription() == null || transaction.getDescription().trim().equals("")) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
    }
    if (transaction.getCategory() == null) { // TODO validate provided category id exists and inform user if not
      validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
    }
    if (transaction.getAccount() == null) { // TODO validate provided account id exists
      validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT_NAME));
    }
    if (transaction.getPrice() == null) {
      validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
    }
    return validationErrors;
  }
}
