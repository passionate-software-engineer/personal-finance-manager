package com.pfm.account.type;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accountTypes")
@CrossOrigin
@Api(tags = {"account-type-controller"})
public interface AccountTypeApi {

  @ApiOperation(value = "Find account type by id", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = AccountType.class),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @GetMapping(value = "/{accountTypeId}")
  ResponseEntity<?> getAccountTypeById(@PathVariable long accountId);

  @ApiOperation(value = "Get list of all account types", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = AccountType.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  @ApiOperation(value = "Create a new account type", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addAccountType(AccountTypeRequest accountTypeRequest);

  @ApiOperation(value = "Update an existing account type", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PutMapping(value = "/{accountTypeId}")
  ResponseEntity<?> updateAccountType(@PathVariable long accountTypeId, AccountTypeRequest accountTypeRequest);

  @ApiOperation(value = "Delete an existing account type", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @DeleteMapping(value = "/{accountTypeId}")
  ResponseEntity<?> deleteAccountType(@PathVariable long accountTypeId);

}

