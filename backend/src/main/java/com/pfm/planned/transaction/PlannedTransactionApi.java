package com.pfm.planned.transaction;

import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;
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

@RequestMapping("plannedTransactions")
@CrossOrigin
@Api(value = "plannedTransaction", description = "Controller used to list / add / update / delete planned transactions.")
public interface PlannedTransactionApi {

  @ApiOperation(value = "Find planned transactions by id", response = Transaction.class, authorizations = {@Authorization(value = "Bearer")})
  @GetMapping(value = "/{plannedTransactionId}")
  ResponseEntity<Transaction> getPlannedTransactionById(@PathVariable long plannedTransactionId);

  @ApiOperation(value = "Get list of all planned transactions", response = Transaction.class, responseContainer = "List",
      authorizations = {@Authorization(value = "Bearer")})
  @GetMapping
  ResponseEntity<List<Transaction>> getPlannedTransactions();

  @ApiOperation(value = "Create a new planned transaction", response = long.class, authorizations = {@Authorization(value = "Bearer")})
  @PostMapping
  ResponseEntity<?> addPlannedTransaction(TransactionRequest plannedTransactionRequest);

  @ApiOperation(value = "Update an existing planned transaction", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @PutMapping(value = "/{plannedTransactionId}")
  ResponseEntity<?> updatePlannedTransaction(@PathVariable long plannedTransactionId, TransactionRequest plannedTransactionRequest);

  @ApiOperation(value = "Delete an existing planned transaction", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @DeleteMapping(value = "/{plannedTransactionId}")
  ResponseEntity<?> deletePlannedTransaction(@PathVariable long plannedTransactionId);
}

