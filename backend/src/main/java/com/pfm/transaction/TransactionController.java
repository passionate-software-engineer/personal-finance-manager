package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
  private CategoryService categoryService;
  private AccountService accountService;

  @ApiOperation(value = "Find transaction by id", response = Transaction.class)
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

  @ApiOperation(value = "Get list of all transactions", response = Transaction.class, responseContainer = "List")
  @GetMapping
  public ResponseEntity<?> getTransactions() {
    log.info("Retrieving all transactions");
    return ResponseEntity.ok(transactionService.getTransactions());
  }

  @ApiOperation(value = "Create a new transaction", response = Long.class)
  @PostMapping
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest) {
    log.info("Adding transaction to the database");

    List<String> validationResult = transactionValidator.validate(transactionRequest);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Optional<Account> transactionAccount = accountService
        .getAccountById(transactionRequest.getAccountId());
    Optional<Category> transactionCategory = categoryService
        .getCategoryById(transactionRequest.getCategoryId());

    Transaction transaction;
    if (transactionAccount.isPresent() && transactionCategory.isPresent()) {
      transaction = Transaction.builder()
          .description(transactionRequest.getDescription())
          .price(transactionRequest.getPrice())
          .account(transactionAccount.get())
          .category(transactionCategory.get())
          .date(transactionRequest.getDate())
          .build();
    } else {
      throw new IllegalStateException();
    }
    Transaction createdTransaction = transactionService.addTransaction(transaction);
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

    log.info("Validating transaction with id {}", id);
    List<String> validationResult = transactionValidator.validate(transactionRequest);

    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Optional<Account> transactionAccount = accountService
        .getAccountById(transactionRequest.getAccountId());
    Optional<Category> transactionCategory = categoryService
        .getCategoryById(transactionRequest.getCategoryId());

    Transaction transaction;
    if (transactionAccount.isPresent() && transactionCategory.isPresent()) {
      transaction = Transaction.builder()
          .description(transactionRequest.getDescription())
          .price(transactionRequest.getPrice())
          .account(transactionAccount.get())
          .category(transactionCategory.get())
          .date(transactionRequest.getDate())
          .build();
    } else {
      throw new IllegalStateException();
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
    transactionService.deleteTransaction(id);

    log.info("Transaction with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TransactionRequest {

    @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 5")
    private String description;

    @ApiModelProperty(value = "Category id", required = true, example = "1")
    private Integer categoryId;

    @ApiModelProperty(value = "Account id", required = true, example = "1")
    private Integer accountId;

    @ApiModelProperty(value = "Price", required = true, example = "15.99")
    private BigDecimal price;

    @ApiModelProperty(value = "Date", required = true, example = "10-02-2018")
    private LocalDate date;

  }
}
