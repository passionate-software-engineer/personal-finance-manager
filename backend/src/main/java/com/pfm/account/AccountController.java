package com.pfm.account;

<<<<<<< HEAD
import static com.pfm.Messages.ACCOUNT_NOT_VALID;
import static com.pfm.Messages.ACCOUNT_WITH_ID;
import static com.pfm.Messages.NOT_FOUND;
import static com.pfm.Messages.UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST;

=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.Messages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
>>>>>>> review
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
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("accounts")
@CrossOrigin
@Api(value = "Accounts", description = "Account Controller")
public class AccountController {

  private AccountService accountService;
  private AccountValidator accountValidator;

  @ApiOperation(value = "Get Accounts with an ID", notes = "Get an account with specific ID")
  @GetMapping(value = "/{id}")
  public ResponseEntity getAccountById(@PathVariable long id) {
    log.info("Retrieving account with ID = ", id);
    Optional<Account> account = accountService.getAccountById(id);

    if (!account.isPresent()) {
      log.info(ACCOUNT_WITH_ID + id + NOT_FOUND);
      return ResponseEntity.notFound().build();
    }
    log.info(ACCOUNT_WITH_ID + id + " successfully retrieved");
    return ResponseEntity.ok(account.get());
  }

  @ApiOperation(value = "Get list of Accounts", notes = "Get all Account in database")
  @GetMapping
  public ResponseEntity<List<Account>> getAccounts() {
    log.info("Retrieving all accounts from database...");
    List<Account> accounts = accountService.getAccounts();
    return ResponseEntity.ok(accounts);
  }

  @ApiOperation(value = "Create a new account", notes = "Creating a new account")
  @PostMapping
  public ResponseEntity addAccount(@RequestBody AccountWithoutId accountWithoutId) {
    log.info("Saving account to the database");
<<<<<<< HEAD

=======
    // must copy as types do not match for Hibernate
    Account account = new Account(null, accountWithoutId.getName(), accountWithoutId.getBalance());
>>>>>>> review
    List<String> validationResult = accountValidator.validate(account);
    if (!validationResult.isEmpty()) {
      log.error(ACCOUNT_NOT_VALID);
      return ResponseEntity.badRequest().body(validationResult);
    }
    Account createdAccount = accountService.addAccount(account);
    log.info("Saving account to the database was successful");
    return ResponseEntity.ok(createdAccount.getId());
  }

  @ApiOperation(value = "Update an account with ID", notes = "Update an account with specific ID")
  @PutMapping(value = "/{id}")
<<<<<<< HEAD
  public ResponseEntity updateAccount(@PathVariable long id, @RequestBody Account account) {
    if (!accountService.idExist(id)) {
      log.info("Updating account : " + UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST);
      return ResponseEntity.notFound().build();
=======
  public ResponseEntity updateAccount(@PathVariable("id") Long id, @RequestBody AccountWithoutId accountWithoutId) {
    if (id == null || !accountService.idExist(id)) {
      log.info("Updating account : " + Messages.UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST);
      return ResponseEntity.badRequest().body(Messages.UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST);
>>>>>>> review
    }
    // must copy as types do not match for Hibernate
    Account account = new Account(id, accountWithoutId.getName(), accountWithoutId.getBalance());
    log.info("Updating account with ID = ", id, " in the database");
    List<String> validationResult = accountValidator.validate(account);

    if (!validationResult.isEmpty()) {
      log.error(ACCOUNT_NOT_VALID);
      return ResponseEntity.badRequest().body(validationResult);
    }
    Account updatedAccount = accountService.updateAccount(id, account);
    log.info(ACCOUNT_WITH_ID + id + " was successfully updated");
    return ResponseEntity.ok(updatedAccount);
  }

<<<<<<< HEAD
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Long> deleteAccount(@PathVariable long id) {
=======
  @ApiOperation(value = "Delete an account", notes = "Deleting an account")
  @DeleteMapping
  public ResponseEntity<Long> deleteAccount(@PathVariable("id") Long id) {
>>>>>>> review
    log.info("Attempting to delete account with ID = " + id);

    if (!accountService.getAccountById(id).isPresent()) {
      log.error(ACCOUNT_WITH_ID + id + NOT_FOUND);
      return ResponseEntity.notFound().build();
    }
    accountService.deleteAccount(id);
    log.info(ACCOUNT_WITH_ID + id, " deleted successfully");
    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  private static class AccountWithoutId extends Account {

    @JsonIgnore
    public void setId(Long id) {
      super.setId(id);
    }
  }
}