package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import java.math.BigDecimal;
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
  private AccountService accountService;

  public Optional<Transaction> getTransactionById(long id) {
    return transactionRepository.findById(id);
  }

  public List<Transaction> getTransactions() {
    return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Transaction::getId))
        .collect(Collectors.toList());
  }

  public Transaction addTransaction(Transaction transaction) {
    subtractAmountToAccountWhileProcessingTransaction(transaction.getAccount().getId(),
        transaction.getPrice());
    return transactionRepository.save(transaction);
  }

  public void updateTransaction(long id, Transaction transaction) {
    Optional<Transaction> transactionFromDb = getTransactionById(id);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException(
          "Transaction with id: " + id + " does not exist in database");
    }

    Transaction transactionToUpdate = transactionFromDb.get();

    addAmountToAccountWhileProcessingTransaction(transactionToUpdate.getAccount().getId(),
        transactionToUpdate.getPrice());

    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategory(transaction.getCategory());
    transactionToUpdate.setPrice(transaction.getPrice());
    transactionToUpdate.setAccount(transaction.getAccount());
    transactionToUpdate.setDate(transaction.getDate());

    subtractAmountToAccountWhileProcessingTransaction(transactionToUpdate.getAccount().getId(),
        transactionToUpdate.getPrice());

    transactionRepository.save(transactionToUpdate);
  }

  public void deleteTransaction(long id) {
    Optional<Transaction> transactionFromDb = getTransactionById(id);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException("Transaction with id: " + id + " not exist in DB");
    }

    Transaction transactionToDelete = transactionFromDb.get();
    addAmountToAccountWhileProcessingTransaction(transactionToDelete.getAccount().getId(),
        transactionToDelete.getPrice());
    transactionRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return transactionRepository.existsById(id);
  }

  private void addAmountToAccountWhileProcessingTransaction(long accountId,
      BigDecimal amountToAdd) {
    Optional<Account> transactionAccount = accountService
        .getAccountById(accountId);

    if (!transactionAccount.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + accountId + " not exist in DB");
    }
    Account accountToUpdate = transactionAccount.get();
    accountToUpdate
        .setBalance(accountToUpdate.getBalance().add(amountToAdd));
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
  }

  private void subtractAmountToAccountWhileProcessingTransaction(long accountId,
      BigDecimal amountToAdd) {
    Optional<Account> transactionAccount = accountService
        .getAccountById(accountId);

    if (!transactionAccount.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + accountId + " not exist in DB");
    }
    Account accountToUpdate = transactionAccount.get();
    accountToUpdate
        .setBalance(accountToUpdate.getBalance().subtract(amountToAdd));
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
  }

}
