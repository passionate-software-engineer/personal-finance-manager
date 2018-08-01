package com.pfm.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("accounts")
@CrossOrigin
@Api(value = "Accounts", description = "Controller used to list / add / update / delete accounts.")
public class AccountController {

  private AccountService accountService;
  private AccountValidator accountValidator;

  @ApiOperation(value = "Find account by id", response = Account.class)
  @GetMapping(value = "/{id}")
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

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List")
  @GetMapping
  public ResponseEntity<List<Account>> getAccounts() {
    log.info("Retrieving all accounts from database");
    List<Account> accounts = accountService.getAccounts();
    return ResponseEntity.ok(accounts);
  }

  @ApiOperation(value = "Create a new account", response = Long.class)
  @PostMapping
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
    log.info("Saving account to the database was successful. Account id is {}",
        createdAccount.getId());
    return ResponseEntity.ok(createdAccount.getId());
  }

  @ApiOperation(value = "Update an existing account", response = Void.class)
  @PutMapping(value = "/{id}")
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

  @ApiOperation(value = "Delete an existing account", response = Void.class)
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteAccount(@PathVariable long id) {
    if (!accountService.getAccountById(id).isPresent()) {
      log.info("No account with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Attempting to delete account with id {}", id);
    accountService.deleteAccount(id);

    log.info("Account with id {} was deleted successfully", id);
    return ResponseEntity.ok().build();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  @Getter
  @Setter
  private static class AccountRequest {

    @ApiModelProperty(value = "Account name", required = true, example = "Alior Bank savings account")
    private String name;

    @ApiModelProperty(value = "Account's balance", required = true, example = "1438.89")
    private BigDecimal balance;
  }
}