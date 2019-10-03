package com.pfm.transaction;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class TransactionController implements TransactionApi {

  static final LocalDate RECURRENCE_PERIOD = LocalDate.now().plusMonths(1L);
  private static final boolean SET_RECURRENT = true;
  private static final boolean SET_NOT_RECURRENT = false;
  private static final String RECURRENT = "recurrent";
  private static final String NOT_RECURRENT = "not recurrent";

  private TransactionsHelper helper;
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

    Transaction createdTransaction = transactionService.addTransaction(userId, transaction, false);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());
    historyEntryService.addHistoryEntryOnAdd(createdTransaction, userId);

    return ResponseEntity.ok(createdTransaction.getId());
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

    Transaction updatingTransaction = helper.convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(updatingTransaction, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transactionToUpdate = originalTransactionOptional.get();
    historyEntryService.addHistoryEntryOnUpdate(transactionToUpdate, updatingTransaction, userId);

    transactionService.updateTransaction(transactionId, userId, updatingTransaction);
    log.info("Transaction with id {} was successfully updated", transactionId);

    return ResponseEntity.ok().build();
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

  @Transactional
  @Override
  public ResponseEntity<?> commitPlannedTransaction(long transactionId) {
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
    Transaction transactionToAdd = getNewInstanceWithCurrentDateAndPlannedStatus(plannedTransaction);
    addAsNewTransaction(transactionToAdd);

    return ResponseEntity.ok(plannedTransaction.getId());
  }

  @Transactional
  @Override
  public ResponseEntity<?> setAsRecurrent(long transactionId) {

    return getResponseEntity(transactionId, SET_RECURRENT, RECURRENT);

  }

  @Transactional
  @Override
  public ResponseEntity<?> setAsNotRecurrent(long transactionId) {

    return getResponseEntity(transactionId, SET_NOT_RECURRENT, NOT_RECURRENT);

  }

  private ResponseEntity<?> getResponseEntity(long transactionId, boolean setAsRecurrent, String loggerMessageOption) {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> transactionOptional = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!transactionOptional.isPresent()) {
      log.info("No transaction with id {} was found, not able to make it {}", transactionId, loggerMessageOption);

      return ResponseEntity.notFound().build();
    }
    Transaction transaction = transactionOptional.get();
    Transaction updatedTransaction = getNewInstanceWithUpdateApplied(transaction, setAsRecurrent);

    return performUpdate(updatedTransaction, userId, setAsRecurrent);
  }

  private void addAsNewTransaction(Transaction transactionToCommit) {
    Transaction newInstance = getNewInstanceWithUpdateApplied(transactionToCommit, true);
    TransactionRequest transactionRequest = helper.convertTransactionToTransactionRequest(transactionToCommit);
    addTransaction(transactionRequest);

    if (newInstance.isRecurrent()) {
      transactionRequest = helper.convertTransactionToTransactionRequest(newInstance);
      addAsNextMonthPlannedTransaction(transactionRequest);
    }

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

  private void addAsNextMonthPlannedTransaction(TransactionRequest transactionRequest) {
    transactionRequest.setDate(RECURRENCE_PERIOD);
    transactionRequest.setPlanned(true);
    addTransaction(transactionRequest);
  }

  private Transaction getNewInstanceWithCurrentDateAndPlannedStatus(Transaction transactionToCommit) {
    Transaction newTransaction = Transaction.builder()
        .date(LocalDate.now())
        .isPlanned(false)
        .userId(transactionToCommit.getUserId())
        .categoryId(transactionToCommit.getCategoryId())
        .description(transactionToCommit.getDescription())
        .accountPriceEntries(getAccountPriceEntriesNewInstance(transactionToCommit))
        .isRecurrent(transactionToCommit.isRecurrent())
        .build();

    return newTransaction;
  }

}
