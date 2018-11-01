package com.pfm.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accounts")
@CrossOrigin
@Api(value = "Accounts", description = "Controller used to list / add / update / delete accounts.")
public interface AccountApi {

  @ApiOperation(value = "Find account by id", response = Account.class)
  @GetMapping(value = "/{accountId}")
  ResponseEntity<?> getAccountById(@PathVariable long accountId, @RequestAttribute long userId);

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Account>> getAccounts(@RequestAttribute long userId);

  @ApiOperation(value = "Create a new account", response = Long.class)
  @PostMapping
  ResponseEntity<?> addAccount(AccountRequest accountRequest, @RequestAttribute long userId);

  @ApiOperation(value = "Update an existing account", response = Void.class) // TODO try to separate response type for each status call 200/400
  @PutMapping(value = "/{accountId}")
  ResponseEntity<?> updateAccount(@PathVariable long accountId, AccountRequest accountRequest, @RequestAttribute long userId);

  @ApiOperation(value = "Delete an existing account", response = Void.class)
  @DeleteMapping(value = "/{accountId}")
  ResponseEntity<?> deleteAccount(@PathVariable long accountId, @RequestAttribute long userId);
}

