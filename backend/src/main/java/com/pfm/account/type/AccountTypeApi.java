package com.pfm.account.type;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accountTypes")
@CrossOrigin
@Api(value = "AccountType", description = "Controller used to list / add / update / delete account type.")
public interface AccountTypeApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Get list of all account types", response = AccountType.class, responseContainer = "List",
      authorizations = {@Authorization(value = BEARER)})
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  @ApiOperation(value = "Create a new account type", response = Long.class, authorizations = {@Authorization(value = BEARER)})
  @PostMapping
  ResponseEntity<?> addAccountType(AccountTypeRequest accountTypeRequest);

}

