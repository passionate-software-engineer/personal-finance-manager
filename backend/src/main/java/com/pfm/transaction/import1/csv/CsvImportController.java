package com.pfm.transaction.import1.csv;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.transaction.DateHelper.DateRange;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import com.pfm.transaction.TransactionsHelper;
import com.pfm.transaction.import1.CsvImportService;
import com.pfm.transaction.import1.DuplicateTransactionService;
import com.pfm.transaction.import1.FileHelper;
import com.pfm.transaction.import1.TransactionsParsingException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@CrossOrigin
@RestController
public class CsvImportController {

  public static final String TEXT_CSV = "text/csv";
  private static final String BEARER = "Bearer";

  private TransactionsFilter transactionsFilter;
  private CsvImportService csvImportService;
  private TransactionService transactionService;
  private TransactionsHelper transactionsHelper;
  private DuplicateTransactionService duplicateTransactionService;
  private AccountService accountService;
  private UserProvider userProvider;
  private FileHelper fileHelper;

  @ApiOperation(value =
      "Upload csv multipartFile with transactions to parse from", response = Transaction.class, responseContainer = "List", authorizations = {
      @Authorization(value = BEARER)})
  @PostMapping("/csvImport")
  ResponseEntity<?> uploadCsvFileAndParseTransactionsToTargetAccount(@RequestParam("file") MultipartFile multipartFile,
      @RequestParam("accountId") long targetAccountId) throws TransactionsParsingException, IOException {

    long userId = userProvider.getCurrentUserId();
    final List<Account> userAccounts = accountService.getAccounts(userId);
    if (userAccounts.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No accounts defined, cannot proceed with csv transactions import");
    }
    if (!accountService.isAccountIdPresentInAccounts(targetAccountId, userAccounts)) {
      String message = String.format("Target account with id %d does not exist", targetAccountId);
      log.info("{}", message);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    Collection<Account> accountsWithoutBankAccountNumber = accountService.getAccountsWithoutBankAccountNumber(userAccounts);
    if (!accountsWithoutBankAccountNumber.isEmpty()) {
      String message = String.format("User accounts %s do not have bank account defined, not able to proceed", accountsWithoutBankAccountNumber);
      log.info(message);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    // FIXME: 29/04/2020 lukasz what about content type
    final String contentType = multipartFile.getContentType();
    File file = fileHelper.convertMultiPartFileToFile(multipartFile);

    log.info("Uploaded file: {}, with size: {}, with content type: {}", file.getName(), file.length(), contentType);

    Collection<Transaction> entriesFromCsv = csvImportService.importTransactions(file, userId, targetAccountId, userAccounts);
    log.info("All {} entries successfully parsed", entriesFromCsv.size());

    Collection<Transaction> filteredEntriesFromCsv = transactionsFilter.discardEntriesWithoutImportId(entriesFromCsv);

    final int filteredOutCsvEntriesCount = entriesFromCsv.size() - filteredEntriesFromCsv.size();
    log.info("{} csv entries filtered out before saving to database", filteredOutCsvEntriesCount);

    Optional<DateRange> dateRangeOptional = transactionsHelper.getDateRangeFromTransactions(filteredEntriesFromCsv);
    final Collection<Transaction> uniqueTransactions = duplicateTransactionService
        .discardTransactionsWithImportIdsAlreadyPresentInDb(filteredEntriesFromCsv, userId, dateRangeOptional);
    final int duplicatesCount = filteredEntriesFromCsv.size() - uniqueTransactions.size();
    log.info("{} parsed transactions discarded before adding to database as they had been already added", duplicatesCount);

    for (Transaction transaction : uniqueTransactions) {
      transactionService.addTransaction(userId, transaction, true);
    }
    final int savedToDatabaseCount = uniqueTransactions.size();
    log.info("{} transactions saved to database", savedToDatabaseCount);

    return ResponseEntity.ok().body(uniqueTransactions);
  }

}
