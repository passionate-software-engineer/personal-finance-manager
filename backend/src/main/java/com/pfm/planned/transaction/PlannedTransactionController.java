package com.pfm.planned.transaction;

import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.GenericTransactionValidator;
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
  private PlannedTransactionService plannedTransactionService;
  private GenericTransactionValidator genericTransactionValidator;
  private HistoryEntryService historyEntryService;
  private AccountService accountService;

  @Override
  public ResponseEntity<PlannedTransaction> getPlannedTransactionById(long plannedTransactionId) {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving planned transaction with id: {}", plannedTransactionId);
    Optional<PlannedTransaction> plannedTransaction = plannedTransactionService.getPlannedTransactionByIdAndUserId(plannedTransactionId, userId);

    if (!plannedTransaction.isPresent()) {
      log.info("Planned transaction with id {} was not found", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Planned transaction with id {} was successfully retrieved", plannedTransactionId);
    return ResponseEntity.ok(plannedTransaction.get());
  }

  @Override
  public ResponseEntity<List<PlannedTransaction>> getPlannedTransactions() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all planned transactions");

    return ResponseEntity.ok(plannedTransactionService.getPlannedTransactions(userId));

  }

  @Override
  @Transactional
  public ResponseEntity<?> addPlannedTransaction(@RequestBody PlannedTransactionRequest plannedTransactionRequest) {
    long userId = userProvider.getCurrentUserId();

    PlannedTransaction plannedTransaction = convertPlannedTransactionRequestToPlannedTransaction(plannedTransactionRequest);

    log.info("Adding  planned transaction to the database");

    //fixme lukasz
    List<String> validationResult = genericTransactionValidator.validate(plannedTransaction, userId);
    if (!validationResult.isEmpty()) {
      log.info("Planned transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    PlannedTransaction createdPlannedTransaction = plannedTransactionService.addPlannedTransaction(userId, plannedTransaction);
    log.info("Saving planned transaction to the database was successful. Planned transaction id is {}", createdPlannedTransaction.getId());

    return ResponseEntity.ok(createdPlannedTransaction.getId());

  }

  @Override
  @Transactional
  public ResponseEntity<?> updatePlannedTransaction(@PathVariable long plannedTransactionId,
      @RequestBody PlannedTransactionRequest plannedTransactionRequest) {
    long userId = userProvider.getCurrentUserId();

    Optional<PlannedTransaction> plannedTransactionByIdAndUserId = plannedTransactionService
        .getPlannedTransactionByIdAndUserId(plannedTransactionId, userId);
    if (!plannedTransactionByIdAndUserId.isPresent()) {
      log.info("No planned transaction with id {} was found, not able to update", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    PlannedTransaction plannedTransaction = convertPlannedTransactionRequestToPlannedTransaction(plannedTransactionRequest);

    List<String> validationResult = genericTransactionValidator.validate(plannedTransaction, userId);
    if (!validationResult.isEmpty()) {
      log.error("Planned transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    plannedTransactionService.updatePlannedTransaction(plannedTransactionId, userId, plannedTransaction);
    log.info("Planned transaction with id {} was successfully updated", plannedTransactionId);

    return ResponseEntity.ok().build();

  }

  @Override
  @Transactional
  public ResponseEntity<?> deletePlannedTransaction(@PathVariable long plannedTransactionId) {
    long userId = userProvider.getCurrentUserId();

    if (!plannedTransactionService.getPlannedTransactionByIdAndUserId(plannedTransactionId, userId).isPresent()) {
      log.info("No planned transaction with id {} was found, not able to delete", plannedTransactionId);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete transaction with id {}", plannedTransactionId);
    plannedTransactionService.deletePlannedTransaction(plannedTransactionId, userId);

    log.info("Planned transaction with id {} was deleted successfully", plannedTransactionId);
    return ResponseEntity.ok().build();

  }

  private PlannedTransaction convertPlannedTransactionRequestToPlannedTransaction(PlannedTransactionRequest plannedTransactionRequest) {
    return new PlannedTransaction(
        plannedTransactionRequest.getDescription(),
        plannedTransactionRequest.getCategoryId(),
        plannedTransactionRequest.getDate(),
        plannedTransactionRequest.getAccountPriceEntries());

  }

}
