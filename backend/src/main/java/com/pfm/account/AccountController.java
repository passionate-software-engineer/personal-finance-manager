package com.pfm.account;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("accounts")
@CrossOrigin
public class AccountController {

  private static final String ACCOUNT_WITH_ID = "Account with ID = ";
  private static final String NOT_FOUND = " was not found!";
  private static final String ACCOUNT_NOT_VALID = "Passed account is not valid!";

  private AccountService accountService;

  private AccountValidator accountValidator;

  @GetMapping(value = "/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) {
    log.info("Retrieving account with ID = ", id);
    Account account = accountService.getAccountById(id);
    if (account == null) {
      log.info(ACCOUNT_WITH_ID + id + NOT_FOUND);
      return ResponseEntity.notFound().build();
    }
    log.info(ACCOUNT_WITH_ID + id + " successfully retrieved");
    return new ResponseEntity<>(account, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<Account>> getAccounts() {
    log.info("Retrieving all accounts from database...");
    List<Account> accounts = accountService.getAccounts();
    return new ResponseEntity<>(accounts, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity addAccount(@RequestBody Account account) {
    log.info("Saving account to the database");
    List<String> validationResult = accountValidator.validate(account);
    if (!validationResult.isEmpty()) {
      log.error(ACCOUNT_NOT_VALID);
      return ResponseEntity.badRequest().body(validationResult);
    }
    Account createdAccount = accountService.addAccount(account);
    log.info("Saving account to the database was successful");
    return new ResponseEntity<>(createdAccount.getId(), HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity updateAccount(@PathVariable("id") Long id,
      @RequestBody Account account) {
    log.info("Updating account with ID = ", id, " in the database");
    List<String> validationResult = accountValidator.validate(account);
    if (!validationResult.isEmpty()) {
      log.error(ACCOUNT_NOT_VALID);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account updatedAccount = accountService.updateAccount(id, account);
    if (updatedAccount == null) {
      log.error(ACCOUNT_WITH_ID + id + NOT_FOUND);
      return ResponseEntity.notFound().build();
    }
    log.info(ACCOUNT_WITH_ID + id + " successfully updated");
    return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Long> deleteAccount(@PathVariable("id") Long id) {
    log.info("Attempting to delete account with ID = " + id);
    if (accountService.getAccountById(id) == null) {
      log.error(ACCOUNT_WITH_ID + id + NOT_FOUND);
      return ResponseEntity.notFound().build();
    }
    accountService.deleteAccount(id);
    log.info(ACCOUNT_WITH_ID + id, " deleted successfully");
    return new ResponseEntity<>(id, HttpStatus.OK);
  }
}