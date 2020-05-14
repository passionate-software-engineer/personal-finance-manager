package com.pfm.transaction;

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
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("transactions")
@CrossOrigin
@Api(tags = {"transaction-controller"})
public interface TransactionApi {

  String TRANSACTION_ID = "{transactionId}";

  @ApiOperation(value = "Find transaction by id", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Transaction.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @GetMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId);

  @ApiOperation(value = "Get list of all transactions", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Transaction.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions();

  @ApiOperation(value = "Create a new transaction", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest);

  @ApiOperation(value = "Update an existing transaction", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PutMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> updateTransaction(@PathVariable long transactionId, TransactionRequest transactionRequest);

  @ApiOperation(value = "Delete an existing transaction", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @DeleteMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> deleteTransaction(@PathVariable long transactionId);

  @ApiOperation(value = "Commits (converts) planned transaction into transaction",
      authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PatchMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> commitPlannedTransaction(@PathVariable long transactionId, TransactionRequest preCommitUpdate);

  @ApiOperation(value = "Sets planned transaction status to recurrent",
      authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE),
  })
  @PatchMapping(value = "/" + TRANSACTION_ID + "/setAsRecurrent")
  ResponseEntity<?> setAsRecurrent(@PathVariable long transactionId, @RequestParam RecurrencePeriod recurrencePeriod);

}
