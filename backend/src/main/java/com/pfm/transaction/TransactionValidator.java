package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_NOT_EXIST;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

//@Component
//public class TransactionValidator {
//
//  @Autowired
//  CategoryService categoryService;
//  @Autowired
//  AccountService accountService;
//  @Autowired
//  TransactionService transactionService;
//
//  public List<String> validate(
//      TransactionRequest transactionRequest) {
//
//    List<String> validationErrors = new ArrayList<>();
//    if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim()
//        .equals("")) {
//      validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
//    }
//
//    if (transactionRequest.getCategoryId() == null) {
//      validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
//    } else {
//      if (!categoryService.idExist(transactionRequest.getCategoryId())) {
//        validationErrors.add(getMessage(CATEGORY_ID_NOT_EXIST));
//      }
//    }
//
//    if (transactionRequest.getAccountId() == null) {
//      validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT_NAME));
//    } else {
//      if (accountService.idExist(transactionRequest.getAccountId())) {
//        validationErrors.add(getMessage(ACCOUNT_ID_NOT_EXIST));
//      }
//    }
//
//    if (transactionRequest.getPrice() == null) {
//      validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
//    }
//    return validationErrors;
//  }
//}

@AllArgsConstructor
@Component
public class TransactionValidator {

  CategoryService categoryService;
  AccountService accountService;
  TransactionService transactionService;

  public List<String> validate(Transaction transaction) {
    List<String> validationResults = new ArrayList<>();

    if (transaction.getDescription() == null || transaction.getDescription().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_TRANSACTION_NAME));
    }
    if (transaction.getCategory() == null) {
      validationResults.add(getMessage(CATEGORY_ID_NOT_EXIST));
    }

    if (transaction.getAccount() == null) {
      validationResults.add(getMessage(ACCOUNT_ID_NOT_EXIST));
    }

    if (transaction.getPrice() == null) {
      validationResults.add(getMessage(EMPTY_TRANSACTION_PRICE));
    }
    return validationResults;
  }
}
