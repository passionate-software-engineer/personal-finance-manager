package com.pfm.currency;

import static com.pfm.swagger.ApiConstants.BEARER;
import static com.pfm.swagger.ApiConstants.CONTAINER_LIST;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("currencies")
@CrossOrigin
@Api(tags = {"currency-controller"})
public interface CurrencyApi {

  @ApiOperation(value = "Get list of all accounts", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Currency.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @GetMapping
  ResponseEntity<List<Currency>> getCurrencies();

}

