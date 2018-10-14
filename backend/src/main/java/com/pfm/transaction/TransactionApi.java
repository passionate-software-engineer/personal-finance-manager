package com.pfm.transaction;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("transactions")
@CrossOrigin
@Api(value = "Transactions", description = "Controller used to list / add / update / delete transaction.")
public interface TransactionApi {

  @ApiOperation(value = "Find transaction by id", response = Transaction.class)
  @GetMapping(value = "/{id}")
  ResponseEntity<Transaction> getTransactionById(long id, long userId);

  @ApiOperation(value = "Get list of all transactions", response = Transaction.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions(long userId);

  @ApiOperation(value = "Create a new transaction", response = long.class)
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest, long userId);

  @ApiOperation(value = "Update an existing transaction", response = Void.class)
  @PutMapping(value = "/{id}")
  ResponseEntity<?> updateTransaction(long id, TransactionRequest transactionRequest, long userId);

  @ApiOperation(value = "Delete an existing transaction", response = Void.class)
  @DeleteMapping(value = "/{id}")
  ResponseEntity<?> deleteTransaction(long id, long userId);
}
