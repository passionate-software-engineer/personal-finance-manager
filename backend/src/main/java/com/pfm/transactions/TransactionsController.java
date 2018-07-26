package com.pfm.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("transactions")
@CrossOrigin
@Api(value = "Transactions", description = "Controller used to list / add / update / delete transactions.")
public class TransactionsController {

    private TransactionsService transactionsService;
    private TransactionsValidator transactionsValidator;

    @ApiOperation(value = "Find Transactions by id", response = Transactions.class)
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTransactionsById(@PathVariable long id) {
        log.info("Retrieving account with id: {}", id);
        Optional<Transactions> transactions = transactionsService.getTransactionsById(id);

        if (!transactions.isPresent()) {
            log.info("Transactions with id {} was not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Transactions with id {} was successfully retrieved", id);
        return ResponseEntity.ok(transactions.get());
    }

    @ApiOperation(value = "Get list of all transactions", response = Transactions.class, responseContainer = "List")
    @GetMapping
    public ResponseEntity<List<Transactions>> getTransactions() {
        log.info("Retrieving all transactions from database");
        List<Transactions> transactions = transactionsService.getTransactions();
        return ResponseEntity.ok(transactions);
    }

    @ApiOperation(value = "Create a new transaction", response = Long.class)
    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody TransactionsRequest transactionsRequest) {
        log.info("Saving transaction {} to the database", transactionsRequest.getTransaction_description());

        // must copy as types do not match for Hibernate
        Transaction transaction = (Transaction) new Transactions(null, transactionsRequest.getTransaction_description(), transactionsRequest.getTransaction_category(), transactionsRequest.getTransaction_account());

        List<String> validationResult = transactionsValidator.validate(transaction);
        if (!validationResult.isEmpty()) {
            log.info("Transaction is not valid {}", validationResult);
            return ResponseEntity.badRequest().body(validationResult);
        }

        Transactions createdTransaction = transactionsService.addTransactions(transaction);
        log.info("Saving transaction to the database was successful. Transaction id is {}",
                createdTransaction.getTransaction_id());
        return ResponseEntity.ok(createdTransaction.getTransaction_id());
    }

    @ApiOperation(value = "Update an existing transaction", response = Void.class)
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id,
                                           @RequestBody TransactionsRequest transactionsRequest) {

        if (!transactionsService.idExist(id)) {
            log.info("No transaction with id {} was found, not able to update", id);
            return ResponseEntity.notFound().build();
        }
        // must copy as types do not match for Hibernate
        Transaction transaction = (Transaction) new Transactions(null, transactionsRequest.getTransaction_description(), transactionsRequest.getTransaction_category(), transactionsRequest.getTransaction_account());

        log.info("Updating transaction with id {}", id);
        List<String> validationResult = transactionsValidator.validate(transaction);

        if (!validationResult.isEmpty()) {
            log.error("Transaction is not valid {}", validationResult);
            return ResponseEntity.badRequest().body(validationResult);
        }

        transactionsService.updateTransactions(id, transaction);
        log.info("Transaction with id {} was successfully updated", id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Delete an existing transaction", response = Void.class)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
        if (!transactionsService.getTransactionsById(id).isPresent()) {
            log.info("No transaction with id {} was found, not able to delete", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Attempting to delete transaction with id {}", id);
        transactionsService.deleteTransctions(id);

        log.info("Transaction with id {} was deleted successfully", id);
        return ResponseEntity.ok().build();
    }

    // hack to ignore id provided by user and not show it in Swagger example
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TransactionsRequest extends Transactions {

        @JsonIgnore
        public void setId(Long id) {
            super.setTransaction_id(id);
        }
    }
}
