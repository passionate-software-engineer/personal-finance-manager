package com.pfm.planned.transaction;

import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.GenericTransactionValidator;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class PlannedTransactionController implements PlannedTransactionApi {

  private UserProvider userProvider;
  private TransactionService transactionService;
  private GenericTransactionValidator genericTransactionValidator;
  private HistoryEntryService historyEntryService;
  private AccountService accountService;

  @Override
  public ResponseEntity<Transaction> getPlannedTransactionById(long plannedTransactionId) {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving planned transaction with id: {}", plannedTransactionId);
    Optional<Transaction> plannedTransaction = transactionService.getTransactionByIdAndUserId(plannedTransactionId, userId);

    if (!plannedTransaction.isPresent()) {
      log.info("Planned transaction with id {} was not found", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Planned transaction with id {} was successfully retrieved", plannedTransactionId);
    return ResponseEntity.ok(plannedTransaction.get());
  }

  @Override
  public ResponseEntity<List<Transaction>> getPlannedTransactions() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all planned transactions");

    return ResponseEntity.ok(transactionService.getTransactions(userId));

  }

  @Override
  @Transactional
  public ResponseEntity<?> addPlannedTransaction(@RequestBody PlannedTransactionRequest plannedTransactionRequest) {
    long userId = userProvider.getCurrentUserId();

    com.pfm.transaction.Transaction plannedTransaction = convertPlannedTransactionRequestToPlannedTransaction(plannedTransactionRequest);

    log.info("Adding  planned transaction to the database");

    List<String> validationResult = genericTransactionValidator.validate(plannedTransaction, userId);
    if (!validationResult.isEmpty()) {
      log.info("Planned transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction createdPlannedTransaction = transactionService.addTransaction(userId, plannedTransaction,true);
    log.info("Saving planned transaction to the database was successful. Planned transaction id is {}", createdPlannedTransaction.getId());

    return ResponseEntity.ok(createdPlannedTransaction.getId());

  }

  @Override
  @Transactional
  public ResponseEntity<?> updatePlannedTransaction(@PathVariable long plannedTransactionId,
      @RequestBody PlannedTransactionRequest plannedTransactionRequest) {
    long userId = userProvider.getCurrentUserId();

    Optional<Transaction> plannedTransactionByIdAndUserId = transactionService.getTransactionByIdAndUserId(plannedTransactionId, userId);
    if (!plannedTransactionByIdAndUserId.isPresent()) {
      log.info("No planned transaction with id {} was found, not able to update", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction plannedTransaction = convertPlannedTransactionRequestToPlannedTransaction(plannedTransactionRequest);

    List<String> validationResult = genericTransactionValidator.validate(plannedTransaction, userId);
    if (!validationResult.isEmpty()) {
      log.error("Planned transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    transactionService.updateTransaction(plannedTransactionId, userId, plannedTransaction);
    log.info("Planned transaction with id {} was successfully updated", plannedTransactionId);

    return ResponseEntity.ok().build();

  }

  @Override
  @Transactional
  public ResponseEntity<?> deletePlannedTransaction(@PathVariable long plannedTransactionId) {
    long userId = userProvider.getCurrentUserId();

    if (!transactionService.getTransactionByIdAndUserId(plannedTransactionId, userId).isPresent()) {
      log.info("No planned transaction with id {} was found, not able to delete", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", plannedTransactionId);
    transactionService.deleteTransaction(plannedTransactionId, userId);

    log.info("Planned transaction with id {} was deleted successfully", plannedTransactionId);
    return ResponseEntity.ok().build();

  }

  //fixme lukasz is that OK?
  private com.pfm.transaction.Transaction convertPlannedTransactionRequestToPlannedTransaction(PlannedTransactionRequest plannedTransactionRequest) {
    return com.pfm.transaction.Transaction.builder()
        .description(plannedTransactionRequest.getDescription())
        .categoryId(plannedTransactionRequest.getCategoryId())
        .date(plannedTransactionRequest.getDate())
        .accountPriceEntries(plannedTransactionRequest.getAccountPriceEntries())
        .build();

  }

}
