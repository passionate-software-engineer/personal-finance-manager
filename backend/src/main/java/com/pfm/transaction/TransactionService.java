package com.pfm.transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

  private TransactionRepository transactionRepository;

  public Optional<Transaction> getTransactionById(long id) {

    return transactionRepository.findById(id);
  }

  public List<Transaction> getTransactions() {
    return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Transaction::getId))
        .collect(Collectors.toList());
  }

  public Transaction addTransactions(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  public void updateTransaction(long id, Transaction transaction) {
    Optional<Transaction> transactionsFromDb = getTransactionById(id);

    if (!transactionsFromDb.isPresent()) {
      throw new IllegalStateException(
          "Transaction with id: " + id + " does not exist in database");
    }

    Transaction transactionToUpdate = transactionsFromDb.get();
    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategory(transaction.getCategory());
    transactionToUpdate.setPrice(transaction.getPrice());
    transactionToUpdate.setAccount(transaction.getAccount());

    transactionRepository.save(transactionToUpdate);
  }

  public void deleteTransctions(long id) {
    transactionRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return transactionRepository.existsById(id);
  }

}
