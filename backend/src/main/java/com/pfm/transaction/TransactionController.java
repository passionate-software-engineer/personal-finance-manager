package com.pfm.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("transactions")
@CrossOrigin
public class TransactionController {

  List<Transaction> transactions = new ArrayList<>();
  int id = 0;

  @GetMapping(value = "/{id}")
  public ResponseEntity<?> getTransactionById(@PathVariable long id) {
    return ResponseEntity.ok(new Transaction());
  }

  @GetMapping
  public ResponseEntity<List<Transaction>> getTransactions() {
    return ResponseEntity.ok(transactions);
  }

  @PostMapping
  public ResponseEntity<?> addTransaction(@RequestBody Transaction transactionRequest) {
    transactionRequest.setId(id++);
    transactions.add(transactionRequest);
    return ResponseEntity.ok(transactionRequest.getId());
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<?> updateTransaction(@PathVariable("id") int id, @RequestBody Transaction transactionRequest) {
    transactions = transactions.stream().filter(transaction -> transaction.getId() != id).collect(Collectors.toList());
    transactionRequest.setId(id);
    transactions.add(transactionRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
    transactions = transactions.stream().filter(transaction -> transaction.getId() != id).collect(Collectors.toList());
    return ResponseEntity.ok().build();
  }


}