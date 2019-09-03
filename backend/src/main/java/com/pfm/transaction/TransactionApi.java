package com.pfm.transaction;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@RequestMapping("transactions")
@CrossOrigin
@Api(value = "Transactions", description = "Controller used to list / add / update / delete transaction.")
public interface TransactionApi {

  @ApiOperation(value = "Find transaction by id", response = Transaction.class, authorizations = {@Authorization(value = "Bearer")})
  @GetMapping(value = "/{transactionId}")
  ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId);

  @ApiOperation(value = "Get list of all transactions", response = Transaction.class, responseContainer = "List",
      authorizations = {@Authorization(value = "Bearer")})
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions();

  @ApiOperation(value = "Get list of all planned transactions", response = Transaction.class, responseContainer = "List",
      authorizations = {@Authorization(value = "Bearer")})
  @GetMapping(value = "/planned")
  ResponseEntity<List<Transaction>> getPlannedTransactions();

  @ApiOperation(value = "Create a new transaction", response = long.class, authorizations = {@Authorization(value = "Bearer")})
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest);

  @ApiOperation(value = "Update an existing transaction", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @PutMapping(value = "/{transactionId}")
  ResponseEntity<?> updateTransaction(@PathVariable long transactionId, TransactionRequest transactionRequest);

  @ApiOperation(value = "Delete an existing transaction", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @DeleteMapping(value = "/{transactionId}")
  ResponseEntity<?> deleteTransaction(@PathVariable long transactionId);
}
