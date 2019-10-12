package com.pfm.transaction;

import static com.pfm.transaction.RecurrencePeriod.EVERY_MONTH;

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

  private static final boolean SET_RECURRENT = true;
  private static final boolean SET_NOT_RECURRENT = false;
  private static final String RECURRENT = "recurrent";
  private static final String NOT_RECURRENT = "not recurrent";

  private TransactionsHelper helper;
  private DateHelper dateHelper;
  private TransactionService transactionService;

  private TransactionValidator transactionValidator;

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

    Transaction transaction = helper.convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(transaction, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    return addTransactionThenLogThenAddHistoryEntry(userId, transaction);
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateTransaction(@PathVariable long transactionId, @RequestBody TransactionRequest transactionRequest) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> originalTransactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!originalTransactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to update", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction transactionUpdate = helper.convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(transactionUpdate, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction originalTransaction = originalTransactionOptional.get();

    final boolean isEligibleForCommit =
        !dateHelper.isPastDate(originalTransaction.getDate()) && (dateHelper.isPastDate(transactionUpdate.getDate()));

    if (isEligibleForCommit) {

      return commitPlannedTransaction(transactionId, transactionRequest);
    }

    historyEntryService.addHistoryEntryOnUpdate(originalTransaction, transactionUpdate, userId);

    transactionService.updateTransaction(transactionId, userId, transactionUpdate);
    log.info("Transaction with id {} was successfully updated", transactionId);

    return ResponseEntity.ok(CommitResult.builder()
        .savedTransactionId(transactionId).build());

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
      @RequestParam(value = "updatingEntry", required = false) TransactionRequest preCommitUpdate) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> plannedTransactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);

    if (!plannedTransactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to commit", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction plannedTransaction = plannedTransactionOptional.get();

    List<String> validationResult = transactionValidator.validate(plannedTransaction, userId);
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
  public ResponseEntity<?> setAsRecurrent(long transactionId) {
    return setRecurrentStatusAndLogWithMessageOption(transactionId, SET_RECURRENT, RECURRENT);

  }

  @Transactional
  @Override
  public ResponseEntity<?> setAsNotRecurrent(long transactionId) {
    return setRecurrentStatusAndLogWithMessageOption(transactionId, SET_NOT_RECURRENT, NOT_RECURRENT);

  }

  private ResponseEntity<?> setRecurrentStatusAndLogWithMessageOption(long transactionId, boolean setAsRecurrent, String loggerMessageOption) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> transactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!transactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to set it {}", transactionId, loggerMessageOption);

      return ResponseEntity.notFound().build();
    }
    Transaction transaction = transactionOptional.get();
    Transaction updatedTransaction = getNewInstanceWithUpdateApplied(transaction, setAsRecurrent);

    return performUpdate(updatedTransaction, userId, setAsRecurrent);
  }

  private CommitResult addAsNewTransaction(long userId, Transaction transactionToCommit) {
    Transaction newInstance = getNewInstanceWithUpdateApplied(transactionToCommit, transactionToCommit.isRecurrent());
    TransactionRequest transactionRequest = helper.convertTransactionToTransactionRequest(transactionToCommit);
    CommitResultBuilder response = CommitResult.builder();

    final Transaction transaction = helper.convertTransactionRequestToTransaction(transactionRequest);
    ResponseEntity<?> createdTransaction = addTransactionThenLogThenAddHistoryEntry(userId, transaction);
    response.savedTransactionId((Long) (createdTransaction.getBody()));

    if (newInstance.isRecurrent()) {
      transactionRequest = helper.convertTransactionToTransactionRequest(newInstance);
      long scheduledForNextMonthId = addAsNextMonthPlannedTransaction(userId, transactionRequest);
      response.recurrentTransactionId(scheduledForNextMonthId);
    }

    return response.build();

  }

  private ResponseEntity<?> performUpdate(Transaction transaction, long userId, boolean updateToBeApplied) {
    long transactionId = transaction.getId();
    log.info("Attempting to set account status as {} with id {} ", updateToBeApplied ? RECURRENT : NOT_RECURRENT, transactionId);
    transactionService.updateTransaction(transactionId, userId, transaction);

    return ResponseEntity.ok().build();

  }

  private Transaction getNewInstanceWithUpdateApplied(Transaction transactionToUpdate, boolean updateToBeApplied) {
    return Transaction.builder()
        .id(transactionToUpdate.getId())
        .description(transactionToUpdate.getDescription())
        .categoryId(transactionToUpdate.getCategoryId())
        .date(transactionToUpdate.getDate())
        .accountPriceEntries(getAccountPriceEntriesNewInstance(transactionToUpdate))
        .userId(transactionToUpdate.getUserId())
        .isPlanned(transactionToUpdate.isPlanned())
        .isRecurrent(updateToBeApplied ? SET_RECURRENT : SET_NOT_RECURRENT)
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

  private ResponseEntity<?> addTransactionThenLogThenAddHistoryEntry(long userId, Transaction transaction) {
    Transaction createdTransaction = transactionService.addTransaction(userId, transaction, false);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());
    historyEntryService.addHistoryEntryOnAdd(createdTransaction, userId);

    return ResponseEntity.ok(createdTransaction.getId());
  }

  private Long addAsNextMonthPlannedTransaction(long userId, TransactionRequest transactionRequest) {
    transactionRequest.setDate(EVERY_MONTH.getValue());
    transactionRequest.setPlanned(true);
    final Transaction transaction = helper.convertTransactionRequestToTransaction(transactionRequest);
    final ResponseEntity<?> response = addTransactionThenLogThenAddHistoryEntry(userId, transaction);

    return (Long) response.getBody();
  }

  private Transaction getNewInstanceWithUpdatedEntriesAndPlannedStatus(Transaction transactionToCommit,
      TransactionRequest preCommitUpdate) {
    Transaction toCommit = preCommitUpdate != null ? helper.convertTransactionRequestToTransaction(preCommitUpdate) : transactionToCommit;

    return Transaction.builder()
        .date(preCommitUpdate != null ? preCommitUpdate.getDate() : LocalDate.now())
        .isPlanned(false)
        .userId(toCommit.getUserId())
        .categoryId(toCommit.getCategoryId())
        .description(toCommit.getDescription())
        .accountPriceEntries(getAccountPriceEntriesNewInstance(toCommit))
        .isRecurrent(toCommit.isRecurrent())
        .build();
  }
}
