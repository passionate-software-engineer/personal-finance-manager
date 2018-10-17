package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class TransactionController implements TransactionApi {

  private TransactionService transactionService;
  private TransactionValidator transactionValidator;
  private CategoryService categoryService;
  private AccountService accountService;

  @Override
  public ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId, @RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving transaction with id: {}", transactionId);
    Optional<Transaction> transaction = transactionService.getTransactionByIdAndUserId(transactionId, userId);

    if (!transaction.isPresent()) {
      log.info("Transaction with id {} was not found", transactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Transaction with id {} was successfully retrieved", transactionId);
    return ResponseEntity.ok(transaction.get());
  }

  @Override
  public ResponseEntity<List<Transaction>> getTransactions(@RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving all transactions");

    return ResponseEntity.ok(transactionService.getTransactions(userId));
  }

  @Override
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest, @RequestAttribute(value = "userId") long userId) {
    log.info("Adding transaction to the database");

    List<String> validationResult = transactionValidator.validate(transactionRequest, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest, userId);

    Transaction createdTransaction = transactionService.addTransaction(userId, transaction);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());

    return ResponseEntity.ok(createdTransaction.getId());
  }

  @Override
  public ResponseEntity<?> updateTransaction(@PathVariable long transactionId, @RequestBody TransactionRequest transactionRequest,
      @RequestAttribute(value = "userId") long userId) {
    if (!transactionService.getTransactionByIdAndUserId(transactionId, userId).isPresent()) {
      log.info("No transaction with id {} was found, not able to update", transactionId);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResult = transactionValidator.validate(transactionRequest, userId);
    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest, userId);

    transactionService.updateTransaction(transactionId, userId, transaction);
    log.info("Transaction with id {} was successfully updated", transactionId);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> deleteTransaction(@PathVariable long transactionId, @RequestAttribute(value = "userId") long userId) {
    if (!transactionService.getTransactionByIdAndUserId(transactionId, userId).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", transactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", transactionId);
    transactionService.deleteTransaction(transactionId, userId);

    log.info("Transaction with id {} was deleted successfully", transactionId);
    return ResponseEntity.ok().build();
  }

  private Transaction convertTransactionRequestToTransaction(TransactionRequest transactionRequest, long userId) {
    Optional<Category> transactionCategory = categoryService.getCategoryByIdAndUserId(transactionRequest.getCategoryId(), userId);
    if (!transactionCategory.isPresent()) {
      throw new IllegalStateException("Provided category id: " + transactionRequest.getCategoryId() + " does not exist in the database");
    }

    for (AccountPriceEntry entry : transactionRequest.getAccountPriceEntries()) {
      Optional<Account> transactionAccount = accountService.getAccountByIdAndUserId(entry.getAccountId(), userId);
      if (!transactionAccount.isPresent()) {
        throw new IllegalStateException("Provided account id: " + entry.getAccountId() + " does not exist in the database");
      }
    }

    return Transaction.builder()
        .description(transactionRequest.getDescription())
        .categoryId(transactionRequest.getCategoryId())
        .date(transactionRequest.getDate())
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .userId(userId)
        .build();
  }

}
