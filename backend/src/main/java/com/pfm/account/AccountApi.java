package com.pfm.account;

import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.BEARER;
import static com.pfm.swagger.ApiConstants.CONTAINER_LIST;
import static com.pfm.swagger.ApiConstants.NOT_FOUND_MESSAGE;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;
import static com.pfm.swagger.ApiConstants.UNAUTHORIZED_MESSAGE;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

  @ApiOperation(value = "Find account by id", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Account.class),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @GetMapping(value = "/{accountId}")
  ResponseEntity<?> getAccountById(@PathVariable long accountId);

  @ApiOperation(value = "Get list of all accounts", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Account.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @GetMapping
  ResponseEntity<List<Account>> getAccounts();

  @ApiOperation(value = "Create a new account", response = Long.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addAccount(AccountRequest accountRequest);

  @ApiOperation(value = "Update an existing account", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PutMapping(value = "/{accountId}")
  ResponseEntity<?> updateAccount(@PathVariable long accountId, AccountRequest accountRequest);

  @ApiOperation(value = "Update an existing account by setting lastVerificationDate to today", authorizations = {
      @Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PatchMapping(value = "/{accountId}/markAccountAsVerifiedToday")
  ResponseEntity<?> markAccountAsVerifiedToday(@PathVariable long accountId);

  @ApiOperation(value = "Archive the account", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PatchMapping(value = "/{accountId}/markAsArchived")
  ResponseEntity<?> markAccountAsArchived(@PathVariable long accountId);

  @ApiOperation(value = "Restore the account", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PatchMapping(value = "/{accountId}/markAsActive")
  ResponseEntity<?> markAccountAsActive(@PathVariable long accountId);

  @ApiOperation(value = "Delete an existing account", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @DeleteMapping(value = "/{accountId}")
  ResponseEntity<?> deleteAccount(@PathVariable long accountId);
}
