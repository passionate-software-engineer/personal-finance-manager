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
  public ResponseEntity<Transaction> getTransactionById(@PathVariable long id, @RequestAttribute(value = "userId") Long userId) {
    log.info("Retrieving transaction with id: {}", id);
    Optional<Transaction> transaction = transactionService.getTransactionByIdAndUserId(id, userId);

    if (!transaction.isPresent()) {
      log.info("Transaction with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Transaction with id {} was successfully retrieved", id);
    return ResponseEntity.ok(transaction.get());
  }

  @Override
  public ResponseEntity<List<Transaction>> getTransactions(@RequestAttribute(value = "userId") Long userId) {
    log.info("Retrieving all transactions");

    return ResponseEntity.ok(transactionService.getTransactions(userId));
  }

  @Override
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest, @RequestAttribute(value = "userId") Long userId) {
    log.info("Adding transaction to the database");

    List<String> validationResult = transactionValidator.validate(transactionRequest, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest, userId);

    Transaction createdTransaction = transactionService.addTransaction(transaction);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());

    return ResponseEntity.ok(createdTransaction.getId());
  }

  @Override
  public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody TransactionRequest transactionRequest,
      @RequestAttribute(value = "userId") Long userId) {
    if (!transactionService.getTransactionByIdAndUserId(id, userId).isPresent()) {
      log.info("No transaction with id {} was found, not able to update", id);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResult = transactionValidator.validate(transactionRequest, userId);
    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest, userId);

    transactionService.updateTransaction(id, transaction);
    log.info("Transaction with id {} was successfully updated", id);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> deleteTransaction(@PathVariable long id, @RequestAttribute(value = "userId") Long userId) {
    if (!transactionService.getTransactionByIdAndUserId(id, userId).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", id);
    transactionService.deleteTransaction(id);

    log.info("Transaction with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  private Transaction convertTransactionRequestToTransaction(TransactionRequest transactionRequest, long userId) {
    Optional<Account> transactionAccount = accountService.getAccountByIdAndUserId(transactionRequest.getAccountId(), userId);
    Optional<Category> transactionCategory = categoryService.getCategoryByIdAndUserId(transactionRequest.getCategoryId(), userId);

    // just double check - it should be already verified by validator
    if (!(transactionAccount.isPresent() && transactionCategory.isPresent())) {
      throw new IllegalStateException("Account or category was not provided");
    }

    return Transaction.builder()
        .description(transactionRequest.getDescription())
        .price(transactionRequest.getPrice())
        .accountId(transactionRequest.getAccountId())
        .categoryId(transactionRequest.getCategoryId())
        .date(transactionRequest.getDate())
        .userId(userId)
        .build();
  }

}
