package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("transactions")
@CrossOrigin
@Api(value = "Transactions", description = "Controller used to list / add / update / delete transaction.")
public class TransactionController {

  private TransactionService transactionService;
  private TransactionValidator transactionValidator;

  @ApiOperation(value = "Find Transaction by id", response = Transaction.class)
  @GetMapping(value = "/{id}")
  public ResponseEntity<?> getTransactionById(@PathVariable long id) {
    log.info("Retrieving transaction with id: {}", id);
    Optional<Transaction> transaction = transactionService.getTransactionById(id);

    if (!transaction.isPresent()) {
      log.info("Transaction with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }
    log.info("Transaction with id {} was successfully retrieved", id);
    return ResponseEntity.ok(transaction.get());
  }

  @ApiOperation(value = "Get list of all transaction", response = Transaction.class, responseContainer = "List")
  @GetMapping
  public ResponseEntity<List<Transaction>> getTransaction() {
    log.info("Retrieving all transaction from database");
    List<Transaction> transaction = transactionService.getTransactions();
    return ResponseEntity.ok(transaction);
  }

  @ApiOperation(value = "Create a new transaction", response = Long.class)
  @PostMapping
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest) {
    log.info("Saving transaction {} to the database", transactionRequest.getDescription());

    // must copy as types do not match for Hibernate
    Transaction transaction = new Transaction(null, transactionRequest.getDescription(),
        transactionRequest.getCategory(), transactionRequest.getPrice(),
        transactionRequest.getAccount());

    List<String> validationResult = transactionValidator.validate(transaction);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction createdTransaction = transactionService.addTransactions(transaction);
    log.info("Saving transaction to the database was successful. Transaction id is {}",
        createdTransaction.getId());
    return ResponseEntity.ok(createdTransaction.getId());
  }

  @ApiOperation(value = "Update an existing transaction", response = Void.class)
  @PutMapping(value = "/{id}")
  public ResponseEntity<?> updateAccount(@PathVariable("id") Long id,
      @RequestBody TransactionRequest transactionRequest) {

    if (!transactionService.idExist(id)) {
      log.info("No transaction with id {} was found, not able to update", id);
      return ResponseEntity.notFound().build();
    }
    // must copy as types do not match for Hibernate
    Transaction transaction = new Transaction(null, transactionRequest.getDescription(),
        transactionRequest.getCategory(), transactionRequest.getPrice(),
        transactionRequest.getAccount());

    log.info("Updating transaction with id {}", id);
    List<String> validationResult = transactionValidator.validate(transaction);

    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    transactionService.updateTransaction(id, transaction);
    log.info("Transaction with id {} was successfully updated", id);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Delete an existing transaction", response = Void.class)
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
    if (!transactionService.getTransactionById(id).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", id);
    transactionService.deleteTransctions(id);

    log.info("Transaction with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  // hack to ignore id provided by user and not show it in Swagger example
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class TransactionRequest extends Transaction {

    @JsonIgnore
    public void setId(Long id) {
      super.setId(id);
    }
  }
}
