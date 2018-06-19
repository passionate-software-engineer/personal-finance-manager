package com.personalfinancemanager.controllers;

import com.personalfinancemanager.services.AccountService;
import lombok.AllArgsConstructor;
import com.personalfinancemanager.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "${rest.mapping.accountController}")
public class AccountController{

  @Autowired
  private AccountService accountService;

  @GetMapping(value = "${rest.mapping.getAccountById}")
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
  public ResponseEntity<Void> addAccount(@RequestBody Account account, UriComponentsBuilder builder) {
    boolean flag = accountService.addAccount(account)!=null;
    if (!flag) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(builder.path("/{id}").buildAndExpand(account.getId()).toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @PutMapping(value = "${rest.mapping.updateAccount}")
  public ResponseEntity<Account> updateAccount(@PathVariable("id") Long id, @RequestBody Account account) {
    accountService.updateAccount(id, account);
    return new ResponseEntity<>(account, HttpStatus.OK);
  }
  @DeleteMapping(value = "${rest.mapping.deleteAccount}")
  public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
    accountService.deleteAccount(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
