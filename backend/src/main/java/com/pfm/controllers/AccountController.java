package com.pfm.controllers;

import com.pfm.model.Account;
import com.pfm.services.AccountService;
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
@RequestMapping(value = "accounts")
@CrossOrigin
public class AccountController {

  @Autowired
  private AccountService accountService;

  @GetMapping(value = "/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) {
    Account account = accountService.getAccountById(id);
    return new ResponseEntity<>(account, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<Account>> getAllAccounts() {
    List<Account> accounts = accountService.getAccounts();
    return new ResponseEntity<>(accounts, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity addAccount(@RequestBody Account account) {
    long id = accountService.addAccount(account).getId();
    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Account> updateAccount(@PathVariable("id") Long id,
      @RequestBody Account account) {
    accountService.updateAccount(id, account);
    return new ResponseEntity<>(account, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
    accountService.deleteAccount(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
