package com.pfm.account.type;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accountType")
@CrossOrigin
@Api(value = "AccountType", description = "Controller used to list / add / update / delete account type.")
public interface AccountTypeApi {

  @ApiOperation(value = "Get list of all accounts", response = AccountType.class, responseContainer = "List",
      authorizations = {@Authorization(value = "Bearer")})
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  // TODO add support for adding, modifying, deleting currencies

  @ApiOperation(value = "Update an existing account type", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  // TODO try to separate response type for each status call 200/400
  @PutMapping(value = "/{accountTypeId}")
  ResponseEntity<?> updateAccountType(@PathVariable long accountTypeId, AccountTypeRequest accountTypeRequest);
}

