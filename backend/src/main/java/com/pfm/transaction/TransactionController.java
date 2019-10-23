package com.pfm.transaction;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.TransactionController.CommitResult.CommitResultBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class TransactionController implements TransactionApi {

  private TransactionsHelper transactionsHelper;
  private DateHelper dateHelper;
  private TransactionValidator transactionValidator;
  private TransactionService transactionService;
  private HistoryEntryService historyEntryService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId) {
    long userId = userProvider.getCurrentUserId();

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
  public ResponseEntity<List<Transaction>> getTransactions() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all transactions");

    return ResponseEntity.ok(transactionService.getTransactions(userId));
  }

  @Override
  @Transactional
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Adding transaction to the database");

    Transaction transaction = transactionsHelper.convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(transaction, userId, null);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    return addTransactionAndHistoryEntryRelatedToIt(userId, transaction);
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateTransaction(@PathVariable long transactionId, @RequestBody TransactionRequest transactionRequest) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> originalTransactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (originalTransactionOptional.isEmpty()) {
      log.info("No transaction with id {} was found, not able to update", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction updatedTransaction = transactionsHelper.convertTransactionRequestToTransaction(transactionRequest);
    Transaction originalTransaction = originalTransactionOptional.get();

    List<String> validationResult = transactionValidator.validate(updatedTransaction, userId, originalTransaction);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    final boolean didDateChangedFromFutureToPast =
        dateHelper.isFutureDate(originalTransaction.getDate()) && (dateHelper.isPastDate(updatedTransaction.getDate()));

    if (didDateChangedFromFutureToPast) {
      return commitPlannedTransaction(transactionId, transactionRequest);
    }

    historyEntryService.addHistoryEntryOnUpdate(originalTransaction, updatedTransaction, userId);

    transactionService.updateTransaction(transactionId, userId, updatedTransaction);
    log.info("Transaction with id {} was successfully updated", transactionId);

    return ResponseEntity.ok(CommitResult.builder()
        .savedTransactionId(transactionId)
        .build());

  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteTransaction(@PathVariable long transactionId) {
    long userId = userProvider.getCurrentUserId();

    final Optional<Transaction> transactionToDeleteOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!transactionToDeleteOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction transactionToDelete = transactionToDeleteOptional.get();
    log.info("Attempting to delete transaction with id {}", transactionId);
    transactionService.deleteTransaction(transactionId, userId);
    historyEntryService.addHistoryEntryOnDelete(transactionToDelete, userId);

    log.info("Transaction with id {} was deleted successfully", transactionId);
    return ResponseEntity.ok().build();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CommitResult {

    private Long savedTransactionId;
    private Long recurrentTransactionId;
  }

  @Transactional
  @Override
  public ResponseEntity<?> commitPlannedTransaction(long transactionId,
      @RequestParam(value = "updatedTransaction", required = false) TransactionRequest preCommitUpdate) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> plannedTransactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);

    if (!plannedTransactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to commit", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction plannedTransaction = plannedTransactionOptional.get();

    List<String> validationResult = transactionValidator.validate(plannedTransaction, userId, null);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    transactionService.deleteTransaction(transactionId, userId);
    Transaction transactionToAdd = getNewInstanceWithUpdatedEntriesAndPlannedStatus(plannedTransaction, preCommitUpdate);

    return ResponseEntity.ok(addAsNewTransaction(userId, transactionToAdd));
  }

  @Transactional
  @Override
  public ResponseEntity<?> setAsRecurrent(long transactionId, @RequestParam RecurrencePeriod recurrencePeriod) {

    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> transactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!transactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to set it recurrent", transactionId);

      return ResponseEntity.notFound().build();
    }
    Transaction transaction = transactionOptional.get();

    transaction.setRecurrencePeriod(recurrencePeriod);

    Transaction updatedTransaction = getNewInstance(transaction);
    log.info("Setting recurrent property of transaction id {} to {}", transactionId, recurrencePeriod);
    transactionService.updateTransaction(transactionId, userId, updatedTransaction);

    return ResponseEntity.ok().build();
  }

  private CommitResult addAsNewTransaction(long userId, Transaction transactionToCommit) {
    Transaction newInstance = getNewInstance(transactionToCommit);
    TransactionRequest transactionRequest = transactionsHelper.convertTransactionToTransactionRequest(transactionToCommit);

    final Transaction transaction = transactionsHelper.convertTransactionRequestToTransaction(transactionRequest);
    ResponseEntity<?> createdTransaction = addTransactionAndHistoryEntryRelatedToIt(userId, transaction);
    CommitResultBuilder response = CommitResult.builder();
    response.savedTransactionId((Long) (createdTransaction.getBody()));

    if (newInstance.isRecurrent()) {
      transactionRequest = transactionsHelper.convertTransactionToTransactionRequest(newInstance);
      long scheduledForNextRecurrentPeriodId = addAsNextRecurrencePeriodPlannedTransaction(userId, transactionRequest);
      response.recurrentTransactionId(scheduledForNextRecurrentPeriodId);
    }

    return response.build();

  }

  private Transaction getNewInstance(Transaction transactionToUpdate) {
    return Transaction.builder()
        .id(transactionToUpdate.getId())
        .description(transactionToUpdate.getDescription())
        .categoryId(transactionToUpdate.getCategoryId())
        .date(transactionToUpdate.getDate())
        .accountPriceEntries(getAccountPriceEntriesNewInstance(transactionToUpdate))
        .userId(transactionToUpdate.getUserId())
        .isPlanned(transactionToUpdate.isPlanned())
        .recurrencePeriod(transactionToUpdate.getRecurrencePeriod())
        .build();
  }

  private List<AccountPriceEntry> getAccountPriceEntriesNewInstance(Transaction transactionToUpdate) {
    return transactionToUpdate.getAccountPriceEntries().stream()
        .map(accountPriceEntry -> AccountPriceEntry.builder()
            .accountId(accountPriceEntry.getAccountId())
            .price(accountPriceEntry.getPrice())
            .build())
        .collect(Collectors.toList());
  }

  private ResponseEntity<?> addTransactionAndHistoryEntryRelatedToIt(long userId, Transaction transaction) {
    Transaction createdTransaction = transactionService.addTransaction(userId, transaction, false);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());
    historyEntryService.addHistoryEntryOnAdd(createdTransaction, userId);

    return ResponseEntity.ok(createdTransaction.getId());
  }

  private Long addAsNextRecurrencePeriodPlannedTransaction(long userId, TransactionRequest transactionRequest) {
    transactionRequest.setDate(transactionRequest.getRecurrencePeriod().getNextOccurrenceDate());
    transactionRequest.setPlanned(true);
    final Transaction transaction = transactionsHelper.convertTransactionRequestToTransaction(transactionRequest);
    final ResponseEntity<?> response = addTransactionAndHistoryEntryRelatedToIt(userId, transaction);

    return (Long) response.getBody();
  }

  private Transaction getNewInstanceWithUpdatedEntriesAndPlannedStatus(Transaction transactionToCommit,
      TransactionRequest preCommitUpdate) {
    Transaction toCommit = preCommitUpdate != null ? transactionsHelper.convertTransactionRequestToTransaction(preCommitUpdate) : transactionToCommit;

    return Transaction.builder()
        .date(preCommitUpdate != null ? preCommitUpdate.getDate() : LocalDate.now())
        .isPlanned(false)
        .userId(toCommit.getUserId())
        .categoryId(toCommit.getCategoryId())
        .description(toCommit.getDescription())
        .accountPriceEntries(getAccountPriceEntriesNewInstance(toCommit))
        .recurrencePeriod(toCommit.getRecurrencePeriod())
        .build();
  }
}
