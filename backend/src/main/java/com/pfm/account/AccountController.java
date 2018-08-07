package com.pfm.account;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AccountController implements AccountApi {

  private AccountService accountService;
  private AccountValidator accountValidator;

  public ResponseEntity<?> getAccountById(@PathVariable long id) {
    log.info("Retrieving account with id: {}", id);
    Optional<Account> account = accountService.getAccountById(id);

    if (!account.isPresent()) {
      log.info("Account with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }
    log.info("Account with id {} was successfully retrieved", id);
    return ResponseEntity.ok(account.get());
  }

  public ResponseEntity<List<Account>> getAccounts() {
    log.info("Retrieving all accounts from database");
    List<Account> accounts = accountService.getAccounts();
    return ResponseEntity.ok(accounts);
  }

  public ResponseEntity<?> addAccount(@RequestBody AccountRequest accountRequest) {
    log.info("Saving account {} to the database", accountRequest.getName());

    // must copy as types do not match for Hibernate
    Account account = new Account(null, accountRequest.getName(), accountRequest.getBalance());

    List<String> validationResult = accountValidator.validateAccountForAdd(account);
    if (!validationResult.isEmpty()) {
      log.info("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account createdAccount = accountService.addAccount(account);
    log.info("Saving account to the database was successful. Account id is {}", createdAccount.getId());
    return ResponseEntity.ok(createdAccount.getId());
  }

  public ResponseEntity<?> updateAccount(@PathVariable("id") Long id,
      @RequestBody AccountRequest accountRequest) {

    if (!accountService.idExist(id)) {
      log.info("No account with id {} was found, not able to update", id);
      return ResponseEntity.notFound().build();
    }
    // must copy as types do not match for Hibernate
    Account account = new Account(id, accountRequest.getName(), accountRequest.getBalance());

    log.info("Updating account with id {}", id);
    List<String> validationResult = accountValidator.validateAccountForUpdate(id, account);

    if (!validationResult.isEmpty()) {
      log.error("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    accountService.updateAccount(id, account);
    log.info("Account with id {} was successfully updated", id);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity<?> deleteAccount(@PathVariable long id) { // TODO deleting account used in transaction / filter throws ugly error
    if (!accountService.getAccountById(id).isPresent()) {
      log.info("No account with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete account with id {}", id);
    accountService.deleteAccount(id);

    log.info("Account with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }
}