package com.pfm.account;

import com.pfm.auth.UserProvider;
import com.pfm.currency.CurrencyService;
import com.pfm.history.HistoryEntryService;
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
public class AccountController implements AccountApi {

  private AccountService accountService;
  private AccountValidator accountValidator;
  private HistoryEntryService historyEntryService;
  private CurrencyService currencyService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<?> getAccountById(@PathVariable long accountId) {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving account with id: {}", accountId);

    Optional<Account> account = accountService.getAccountByIdAndUserId(accountId, userId);

    if (!account.isPresent()) {
      log.info("Account with id {} was not found", accountId);
      return ResponseEntity.notFound().build();
    }
    log.info("Account with id {} was successfully retrieved", accountId);
    return ResponseEntity.ok(account.get());
  }

  @Override
  public ResponseEntity<List<Account>> getAccounts() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all accounts from database");

    List<Account> accounts = accountService.getAccounts(userId);
    return ResponseEntity.ok(accounts);
  }

  @Override
  @Transactional
  public ResponseEntity<?> addAccount(@RequestBody AccountRequest accountRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Saving account {} to the database", accountRequest.getName());

    Account account = convertAccountRequestToAccount(accountRequest, userId);

    List<String> validationResult = accountValidator.validateAccountIncludingNameDuplication(userId, account);
    if (!validationResult.isEmpty()) {
      log.info("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account createdAccount = accountService.addAccount(userId, account);
    log.info("Saving account to the database was successful. Account id is {}", createdAccount.getId());
    historyEntryService.addHistoryEntryOnAdd(createdAccount, userId);
    return ResponseEntity.ok(createdAccount.getId());
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateAccount(@PathVariable long accountId, @RequestBody AccountRequest accountRequest) {
    long userId = userProvider.getCurrentUserId();

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to update", accountId);
      return ResponseEntity.notFound().build();
    }
    Account account = convertAccountRequestToAccount(accountRequest, userId);

    log.info("Updating account with id {}", accountId);
    List<String> validationResult = accountValidator.validateAccountForUpdate(accountId, userId, account);

    if (!validationResult.isEmpty()) {
      log.error("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account accountToUpdate = accountService.getAccountByIdAndUserId(accountId, userId).get();

    historyEntryService.addHistoryEntryOnUpdate(accountToUpdate, account, userId);
    accountService.updateAccount(accountId, userId, account);

    log.info("Account with id {} was successfully updated", accountId);

    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteAccount(@PathVariable long accountId) {
    long userId = userProvider.getCurrentUserId();

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to delete", accountId);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResults = accountValidator.validateAccountForDelete(accountId);
    if (!validationResults.isEmpty()) {
      log.info("Account with id {} was found, in transaction or filter, not able to delete", accountId);
      return ResponseEntity.badRequest().body(validationResults);
    }

    Account account = accountService.getAccountByIdAndUserId(accountId, userId).get();
    historyEntryService.addHistoryEntryOnDelete(account, userId);
    log.info("Attempting to delete account with id {}", accountId);
    accountService.deleteAccount(accountId);

    log.info("Account with id {} was deleted successfully", accountId);

    return ResponseEntity.ok().build();
  }

  private Account convertAccountRequestToAccount(AccountRequest accountRequest, long userId) {
    return Account.builder()
        .name(accountRequest.getName())
        .balance(accountRequest.getBalance())
        .currency(currencyService.getCurrencyByIdAndUserId(accountRequest.getCurrencyId(), userId))
        .build();
  }
}