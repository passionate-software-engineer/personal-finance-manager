package com.pfm.account;

import com.pfm.history.HistoryEntryService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AccountController implements AccountApi {

  private AccountService accountService;
  private AccountValidator accountValidator;
  private HistoryEntryService historyEntryService;

  public ResponseEntity<?> getAccountById(@PathVariable long accountId, @RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving account with id: {}", accountId);

    Optional<Account> account = accountService.getAccountByIdAndUserId(accountId, userId);

    if (!account.isPresent()) {
      log.info("Account with id {} was not found", accountId);
      return ResponseEntity.notFound().build();
    }
    log.info("Account with id {} was successfully retrieved", accountId);
    return ResponseEntity.ok(account.get());
  }

  public ResponseEntity<List<Account>> getAccounts(@RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving all accounts from database");

    List<Account> accounts = accountService.getAccounts(userId);
    return ResponseEntity.ok(accounts);
  }

  public ResponseEntity<?> addAccount(@RequestBody AccountRequest accountRequest, @RequestAttribute(value = "userId") long userId) {
    log.info("Saving account {} to the database", accountRequest.getName());

    //TODO add convert Account Request to Account method
    Account account = new Account(null, accountRequest.getName(), accountRequest.getBalance(), userId);

    List<String> validationResult = accountValidator.validateAccountIncludingNameDuplication(userId, account);
    if (!validationResult.isEmpty()) {
      log.info("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account createdAccount = accountService.addAccount(account);
    log.info("Saving account to the database was successful. Account id is {}", createdAccount.getId());
    historyEntryService.addEntryOnAdd(createdAccount, userId);
    return ResponseEntity.ok(createdAccount.getId());
  }

  public ResponseEntity<?> updateAccount(@PathVariable long accountId,
      @RequestBody AccountRequest accountRequest, @RequestAttribute(value = "userId") long userId) {

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to update", accountId);
      return ResponseEntity.notFound().build();
    }
    Account account = new Account(accountId, accountRequest.getName(), accountRequest.getBalance(), userId);

    log.info("Updating account with id {}", accountId);
    List<String> validationResult = accountValidator.validateAccountForUpdate(accountId, userId, account);

    if (!validationResult.isEmpty()) {
      log.error("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account accountToUpdate = accountService.getAccountByIdAndUserId(accountId, userId).get();
    historyEntryService.addEntryOnUpdate(accountToUpdate, account, userId);

    accountService.updateAccount(accountId, userId, account);

    log.info("Account with id {} was successfully updated", accountId);

    return ResponseEntity.ok().build();
  }

  public ResponseEntity<?> deleteAccount(@PathVariable long accountId,
      @RequestAttribute(value = "userId") long userId) { // TODO deleting account used in transaction / filter throws ugly error

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to delete", accountId);
      return ResponseEntity.notFound().build();
    }
    Account account = accountService.getAccountByIdAndUserId(accountId, userId).get();
    log.info("Attempting to delete account with id {}", accountId);
    accountService.deleteAccount(accountId);

    historyEntryService.addEntryOnDelete(account, userId);
    log.info("Account with id {} was deleted successfully", accountId);

    return ResponseEntity.ok().build();
  }
}