package com.pfm.transaction;

import static com.pfm.helpers.TransactionHelper.convertTransactionToTransactionRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.auth.TokenService;
import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class TransactionController implements TransactionApi {

  private static final String SERVER_URI = "http://localhost:8088";
  private static final String TRANSACTIONS_SERVICE_PATH = "/transactions";

  @Qualifier("pfmObjectMapper")
  @Autowired
  protected ObjectMapper mapper;
  private TokenService tokenService;
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
  public ResponseEntity<List<Transaction>> getPlannedTransactions() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all planned transactions");

    return ResponseEntity.ok(transactionService.getPlannedTransactions(userId));
  }

  @Override
  @Transactional
  public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest transactionRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Adding transaction to the database");

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(transaction, userId);
    if (!validationResult.isEmpty()) {
      log.info("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }
    Transaction createdTransaction = transactionService.addTransaction(userId, transaction, transaction.isPlanned());
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdTransaction.getId());
    if (!createdTransaction.isPlanned()) {
      historyEntryService.addHistoryEntryOnAdd(createdTransaction, userId);
    }

    return ResponseEntity.ok(createdTransaction.getId());
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateTransaction(@PathVariable long transactionId, @RequestBody TransactionRequest transactionRequest) {
    long userId = userProvider.getCurrentUserId();

    Optional<Transaction> transactionByIdAndUserId = transactionService.getTransactionByIdAndUserId(transactionId, userId);
    if (!transactionByIdAndUserId.isPresent()) {
      log.info("No transaction with id {} was found, not able to update", transactionId);
      return ResponseEntity.notFound().build();
    }

    Transaction transaction = convertTransactionRequestToTransaction(transactionRequest);

    List<String> validationResult = transactionValidator.validate(transaction, userId);
    if (!validationResult.isEmpty()) {
      log.error("Transaction is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Transaction transactionToUpdate = transactionService.getTransactionByIdAndUserId(transactionId, userId).get(); // TODO add .isPresent
    if (!transactionToUpdate.isPlanned()) {
      historyEntryService.addHistoryEntryOnUpdate(transactionToUpdate, transaction, userId);
    }

    transactionService.updateTransaction(transactionId, userId, transaction);
    log.info("Transaction with id {} was successfully updated", transactionId);

    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteTransaction(@PathVariable long transactionId) {
    long userId = userProvider.getCurrentUserId();

    if (!transactionService.getTransactionByIdAndUserId(transactionId, userId).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", transactionId);
      return ResponseEntity.notFound().build();
    }
    Transaction transactionToDelete = transactionService.getTransactionByIdAndUserId(transactionId, userId).get(); // TODO add .isPresent
    log.info("Attempting to delete transaction with id {}", transactionId);
    transactionService.deleteTransaction(transactionId, userId);
    if (!transactionToDelete.isPlanned()) {
      historyEntryService.addHistoryEntryOnDelete(transactionToDelete, userId);
    }

    log.info("Transaction with id {} was deleted successfully", transactionId);
    return ResponseEntity.ok().build();
  }

  private Transaction convertTransactionRequestToTransaction(TransactionRequest transactionRequest) {
    return Transaction.builder()
        .description(transactionRequest.getDescription())
        .categoryId(transactionRequest.getCategoryId())
        .date(transactionRequest.getDate())
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .isPlanned(transactionRequest.isPlanned())
        .build();
  }

  @Transactional
  @Override
  public ResponseEntity<?> commitPlannedTransaction(long transactionId) throws Exception {
    long userId = userProvider.getCurrentUserId();
    Optional<Transaction> optionalPlannedTransaction = transactionService.getTransactionByIdAndUserId(transactionId, userId);

    if (!optionalPlannedTransaction.isPresent()) {
      log.info("No transaction with id {} was found, not able to commit", transactionId);
      return ResponseEntity.notFound().build();
    }
    Transaction plannedTransaction = optionalPlannedTransaction.get();
    deletePlannedTransaction(transactionId, userId);
    Transaction transactionToAdd = applyCurrentDateAndClearPlannedStatus(plannedTransaction);

    addAsNewTransaction(userId, transactionToAdd);

    return ResponseEntity.ok().build();
  }

  private void addAsNewTransaction(long userId, Transaction transactionToCommit) throws Exception {
    String currentUserAccessToken = tokenService.getTokensByUserId().get(userId).getAccessToken().getValue();
    TransactionRequest transactionRequest = convertTransactionToTransactionRequest(transactionToCommit);

    HttpRequest postRequest = HttpRequest.newBuilder()
        .uri(URI.create(SERVER_URI + TRANSACTIONS_SERVICE_PATH))
        .header("Content-Type", "application/json")
        .header("Authorization", currentUserAccessToken)
        .POST(BodyPublishers.ofString(json(transactionRequest)))
        .build();

    HttpResponse<Void> response = HttpClient.newHttpClient()
        .send(postRequest, HttpResponse.BodyHandlers.discarding());

  }

  private Transaction applyCurrentDateAndClearPlannedStatus(Transaction transactionToCommit) {
    transactionToCommit.setDate(LocalDate.from(LocalDate.now()));
    transactionToCommit.setPlanned(false);
    return transactionToCommit;
  }

  private void deletePlannedTransaction(long transactionId, long userId) {
    transactionService.deleteTransaction(transactionId, userId);
  }

  protected String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }
}
