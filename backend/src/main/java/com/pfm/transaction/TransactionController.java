package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Transaction> getTransactionById(long id) {
    log.info("Retrieving transaction with id: {}", id);
    Optional<TransactionPojo> transaction = transactionService.getTransactionById(id);

    if (!transaction.isPresent()) {
      log.info("TransactionPojo with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }

    log.info("TransactionPojo with id {} was successfully retrieved", id);
    return ResponseEntity.ok(convertTransactionToTransactionResponse(transaction.get()));
  }

  @Override
  public ResponseEntity<List<Transaction>> getTransactions() {
    log.info("Retrieving all transactions");

    List<Transaction> transactions = transactionService.getTransactions().stream()
        .map(this::convertTransactionToTransactionResponse)
        .collect(Collectors.toList());

    return ResponseEntity.ok(transactions);
  }

  @Override
  public ResponseEntity<?> addTransaction(@RequestBody Transaction transactionRequest) {
    log.info("Adding transaction to the database");

    List<String> validationResult = transactionValidator.validate(transactionRequest);
    if (!validationResult.isEmpty()) {
      log.info("TransactionPojo is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    TransactionPojo transaction = convertTransactionRequestToTransaction(transactionRequest);

    TransactionPojo createdTransaction = transactionService.addTransaction(transaction);
    log.info("Saving transaction to the database was successful. TransactionPojo id is {}", createdTransaction.getId());

    return ResponseEntity.ok(createdTransaction.getId());
  }


  @Override
  public ResponseEntity<?> updateTransaction(long id, @RequestBody Transaction transactionRequest) {
    if (!transactionService.idExist(id)) {
      log.info("No transaction with id {} was found, not able to update", id);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResult = transactionValidator.validate(transactionRequest);
    if (!validationResult.isEmpty()) {
      log.error("TransactionPojo is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    TransactionPojo transaction = convertTransactionRequestToTransaction(transactionRequest);

    transactionService.updateTransaction(id, transaction);
    log.info("TransactionPojo with id {} was successfully updated", id);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> deleteTransaction(long id) {
    if (!transactionService.getTransactionById(id).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", id);
    transactionService.deleteTransaction(id);

    log.info("TransactionPojo with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  private TransactionPojo convertTransactionRequestToTransaction(Transaction transactionRequest) {
    Optional<Account> transactionAccount = accountService.getAccountById(transactionRequest.getAccountId());
    Optional<Category> transactionCategory = categoryService.getCategoryById(transactionRequest.getCategoryId());

    // just double check - it should be already verified by validator
    if (!(transactionAccount.isPresent() && transactionCategory.isPresent())) {
      throw new IllegalStateException("Account or category was not provided");
    }

    return TransactionPojo.builder()
        .description(transactionRequest.getDescription())
        .price(transactionRequest.getPrice())
        .account(transactionAccount.get())
        .category(transactionCategory.get())
        .date(transactionRequest.getDate())
        .build();
  }

  private Transaction convertTransactionToTransactionResponse(TransactionPojo transaction) {
    return Transaction.builder()
        .description(transaction.getDescription())
        .price(transaction.getPrice())
        .accountId(transaction.getAccount().getId())
        .categoryId(transaction.getCategory().getId())
        .date(transaction.getDate())
        .build();
  }

}
