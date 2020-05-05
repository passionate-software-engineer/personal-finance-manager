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

  @ApiOperation(value = "Get list of all account types", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = AccountType.class, responseContainer = "list"),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  @ApiOperation(value = "Create a new account type", response = Long.class, authorizations = {@Authorization(value = BEARER)})
  @PostMapping
  ResponseEntity<?> addAccountType(AccountTypeRequest accountTypeRequest);

}

