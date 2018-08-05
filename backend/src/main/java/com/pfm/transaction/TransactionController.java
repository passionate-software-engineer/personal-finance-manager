package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    transactions.add(transactionRequest);
    return ResponseEntity.ok(0);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id,
      @RequestBody Transaction transactionRequest) {

    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
    return ResponseEntity.ok().build();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  @Getter
  @Setter
  private static class TransactionRequest {

    @ApiModelProperty(value = "Transaction name", required = true, example = "Alior Bank savings transaction")
    private String name;

    @ApiModelProperty(value = "Transaction's balance", required = true, example = "1438.89")
    private BigDecimal balance;
  }
}