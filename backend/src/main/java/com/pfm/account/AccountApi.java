package com.pfm.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accounts")
@CrossOrigin
@Api(value = "Accounts", description = "Controller used to list / add / update / delete accounts.")
public interface AccountApi {

  @ApiOperation(value = "Find account by id", response = Account.class)
  @GetMapping(value = "/{id}")
  ResponseEntity<?> getAccountById(long id, long userId);

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List")
  @GetMapping
  public ResponseEntity<List<Account>> getAccounts(long userId);

  @ApiOperation(value = "Create a new account", response = Long.class)
  @PostMapping
  public ResponseEntity<?> addAccount(AccountRequest accountRequest, long userId);

  @ApiOperation(value = "Update an existing account", response = Void.class)
  @PutMapping(value = "/{id}")
  public ResponseEntity<?> updateAccount(Long id, AccountRequest accountRequest, long userId);

  @ApiOperation(value = "Delete an existing account", response = Void.class)
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteAccount(long id, long userId);
}

