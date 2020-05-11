package com.pfm.currency;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("currencies")
@CrossOrigin
@Api(tags = {"currency-controller"})
public interface CurrencyApi {

  @ApiOperation(value = "Get list of all accounts", authorizations = {@Authorization(value = "Bearer")})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Currency.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
  })
  @GetMapping
  ResponseEntity<List<Currency>> getCurrencies();

}

