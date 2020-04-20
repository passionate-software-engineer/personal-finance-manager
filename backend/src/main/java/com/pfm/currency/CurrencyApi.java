package com.pfm.currency;

import com.pfm.account.Account;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("currencies")
@CrossOrigin
@Api(value = "Currencies", description = "Controller used to list / add / update / delete currencies.")
public interface CurrencyApi {

  @ApiOperation(value = "Get list of all accounts", response = Account.class, responseContainer = "List",
      authorizations = {@Authorization(value = "Bearer")})
  @GetMapping
  ResponseEntity<List<Currency>> getCurrencies();

}

