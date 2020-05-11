package com.pfm.account.type;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accountTypes")
@CrossOrigin
@Api(tags = {"account-type-controller"})
public interface AccountTypeApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Get list of all account types", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = AccountType.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
  })
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  @ApiOperation(value = "Create a new account type", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Long.class),
      @ApiResponse(code = 400, message = "Error", response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addAccountType(AccountTypeRequest accountTypeRequest);

}

