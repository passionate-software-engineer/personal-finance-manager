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
  public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
    log.info("Retrieving transaction with id: {}", id);
    Optional<Transaction> transaction = transactionService.getTransactionById(id);

    if (!transaction.isPresent()) {
      log.info("Transaction with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Transaction with id {} was successfully retrieved", id);
    return ResponseEntity.ok(transaction.get());
  }

  @Override
  public ResponseEntity<List<Transaction>> getTransactions() {
    log.info("Retrieving all transactions");

    return ResponseEntity.ok(transactionService.getTransactions());
  }

  @Override
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest) {
    log.info("Adding transaction to the database");

    List<String> validationResult = transactionValidator.validate(transactionRequest);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest);

    Transaction createdTransaction = transactionService.addTransaction(transaction);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());

    return ResponseEntity.ok(createdTransaction.getId());
  }

  @Override
  public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody TransactionRequest transactionRequest) {
    if (!transactionService.idExist(id)) {
      log.info("No transaction with id {} was found, not able to update", id);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResult = transactionValidator.validate(transactionRequest);
    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest);

    transactionService.updateTransaction(id, transaction);
    log.info("Transaction with id {} was successfully updated", id);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
    if (!transactionService.getTransactionById(id).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", id);
    transactionService.deleteTransaction(id);

    log.info("Transaction with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  private Transaction convertTransactionRequestToTransaction(TransactionRequest transactionRequest) {
    // TODO - why do we need that check if it's done in validator?
    Optional<Category> transactionCategory = categoryService.getCategoryById(transactionRequest.getCategoryId());

    // just double check - it should be already verified by validator (if method is called without checking with validator)
    if (!transactionCategory.isPresent()) {
      throw new IllegalStateException("Provided category does not exists in the database");
    }

    for (AccountPriceEntry entry : transactionRequest.getAccountPriceEntries()) {
      Optional<Account> transactionAccount = accountService.getAccountById(entry.getAccountId());
      if (!transactionAccount.isPresent()) {
        throw new IllegalStateException("Provided account does not exists in the database");
      }
    }

    return Transaction.builder()
        .description(transactionRequest.getDescription())
        .categoryId(transactionRequest.getCategoryId())
        .date(transactionRequest.getDate())
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .build();
  }

}
