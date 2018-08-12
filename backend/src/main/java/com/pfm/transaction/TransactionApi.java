package com.pfm.transaction;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
public interface TransactionApi { // TODO extract API for other services

  @ApiOperation(value = "Find transaction by id", response = Transaction.class)
  @GetMapping(value = "/{id}")
  ResponseEntity<Transaction> getTransactionById(long id);

  @ApiOperation(value = "Get list of all transactions", response = Transaction.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions();

  @ApiOperation(value = "Create a new transaction", response = Long.class)
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest);

  @ApiOperation(value = "Update an existing transaction", response = Void.class)
  @PutMapping(value = "/{id}")
  ResponseEntity<?> updateTransaction(long id, TransactionRequest transactionRequest);

  @ApiOperation(value = "Delete an existing transaction", response = Void.class)
  @DeleteMapping(value = "/{id}")
  ResponseEntity<?> deleteTransaction(long id);
}
