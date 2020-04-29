package com.pfm.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accounts")
@CrossOrigin
@Api(tags = {"account-controller"})
public interface AccountApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Find account by id", response = Account.class, authorizations = {@Authorization(value = BEARER)})
  @GetMapping(value = "/{accountId}")
  ResponseEntity<?> getAccountById(@PathVariable long accountId);

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List",
      authorizations = {@Authorization(value = BEARER)})
  @GetMapping
  ResponseEntity<List<Account>> getAccounts();

  @ApiOperation(value = "Create a new account", response = Long.class, authorizations = {@Authorization(value = BEARER)})
  @PostMapping
  ResponseEntity<?> addAccount(AccountRequest accountRequest);

  @ApiOperation(value = "Update an existing account", authorizations = {@Authorization(value = BEARER)})
  // TODO Swagger try to separate response type for each status call 200/400
  @PutMapping(value = "/{accountId}")
  ResponseEntity<?> updateAccount(@PathVariable long accountId, AccountRequest accountRequest);

  @ApiOperation(value = "Update an existing account by setting lastVerificationDate to today", response = Void.class, authorizations = {
      @Authorization(value = BEARER)})
  @PatchMapping(value = "/{accountId}/markAccountAsVerifiedToday")
  ResponseEntity<?> markAccountAsVerifiedToday(@PathVariable long accountId);

  @ApiOperation(value = "Archive the account", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @PatchMapping(value = "/{accountId}/markAsArchived")
  ResponseEntity<?> markAccountAsArchived(@PathVariable long accountId);

  @ApiOperation(value = "Restore the account", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @PatchMapping(value = "/{accountId}/markAsActive")
  ResponseEntity<?> markAccountAsActive(@PathVariable long accountId);

  @ApiOperation(value = "Delete an existing account", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @DeleteMapping(value = "/{accountId}")
  ResponseEntity<?> deleteAccount(@PathVariable long accountId);
}
