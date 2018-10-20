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
  @GetMapping(value = "/{accountId}")
  ResponseEntity<?> getAccountById(long accountId, long userId);

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Account>> getAccounts(long userId);

  @ApiOperation(value = "Create a new account", response = Long.class)
  @PostMapping
  ResponseEntity<?> addAccount(AccountRequest accountRequest, long userId);

  @ApiOperation(value = "Update an existing account", response = Void.class)
  @PutMapping(value = "/{accountId}")
  ResponseEntity<?> updateAccount(long accountId, AccountRequest accountRequest, long userId);

  @ApiOperation(value = "Delete an existing account", response = Void.class)
  @DeleteMapping(value = "/{accountId}")
  ResponseEntity<?> deleteAccount(long accountId, long userId);
}

