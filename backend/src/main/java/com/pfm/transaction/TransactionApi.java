package com.pfm.transaction;

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

  String BEARER = "Bearer";
  String TRANSACTION_ID = "{transactionId}";

  @ApiOperation(value = "Find transaction by id", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Transaction.class),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
  })
  @GetMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId);

  @ApiOperation(value = "Get list of all transactions", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Transaction.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
      @ApiResponse(code = 404, message = "Bad request", response = Void.class),
  })
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions();

  @ApiOperation(value = "Create a new transaction", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Long.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest);

  @ApiOperation(value = "Update an existing transaction", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 400, message = "Error", response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
      @ApiResponse(code = 404, message = "Bad request", response = Void.class),
  })
  @PutMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> updateTransaction(@PathVariable long transactionId, TransactionRequest transactionRequest);

  @ApiOperation(value = "Delete an existing transaction", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
      @ApiResponse(code = 404, message = "Bad request", response = Void.class),
  })
  @DeleteMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> deleteTransaction(@PathVariable long transactionId);

  @ApiOperation(value = "Commits (converts) planned transaction into transaction", response = Void.class,
      authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
      @ApiResponse(code = 404, message = "Bad request", response = Void.class),
  })
  @PatchMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> commitPlannedTransaction(@PathVariable long transactionId, TransactionRequest preCommitUpdate);

  @ApiOperation(value = "Sets planned transaction status to recurrent", response = Void.class,
      authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
      @ApiResponse(code = 404, message = "Bad request", response = Void.class),
  })
  @PatchMapping(value = "/" + TRANSACTION_ID + "/setAsRecurrent")
  ResponseEntity<?> setAsRecurrent(@PathVariable long transactionId, @RequestParam RecurrencePeriod recurrencePeriod);

}
