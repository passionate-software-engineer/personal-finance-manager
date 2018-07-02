package com.pfm.account;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private AccountService accountService;

  @GetMapping(value = "/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) {
    Account account = accountService.getAccountById(id);
    Preconditions.checkNotNull(account, "Could not get account with id %s", id);
    return new ResponseEntity<>(account, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<Account>> getAccounts() {
    List<Account> accounts = accountService.getAccounts();
    Preconditions.checkNotNull(accounts,
        "Could not get accounts");
    return new ResponseEntity<>(accounts, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity addAccount(@RequestBody Account account) {
    Preconditions.checkNotNull(account,
        "Invalid account");
    long id = accountService.addAccount(account).getId();
    Preconditions.checkNotNull(id,
        "Could not add account");
    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Account> updateAccount(@PathVariable("id") Long id,
      @RequestBody Account account) {
    Preconditions.checkNotNull(id,
        "Could not find account with id %s", id);
    Account updatedAccount = accountService.updateAccount(id, account);
    Preconditions.checkNotNull(updatedAccount,
        "Could not update account with id %s", id);
    return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
    Preconditions.checkNotNull(id,
        "Could not find account with id %s", id);
    accountService.deleteAccount(id);
    Preconditions.checkArgument(getAccountById(id) == null,
        "Could not delete account with id %s", id );
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
