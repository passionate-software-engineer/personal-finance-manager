package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
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
    Optional<Account> transactionAccount = accountService
        .getAccountById(transaction.getAccount().getId());
    if (!transactionAccount.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + transaction.getAccount().getId() + " not exist in DB");
    }
    Account accountToUpdate = transactionAccount.get();
    accountToUpdate.setBalance(accountToUpdate.getBalance().subtract(transaction.getPrice()));
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
    return transactionRepository.save(transaction);
  }

  public void updateTransaction(long id, Transaction transaction) {
    Optional<Transaction> transactionFromDb = getTransactionById(id);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException(
          "Transaction with id: " + id + " does not exist in database");
    }

    Transaction transactionToUpdate = transactionFromDb.get();

    Optional<Account> transactionAccountBeforeUpdate = accountService
        .getAccountById(transactionToUpdate.getAccount().getId());

    if (!transactionAccountBeforeUpdate.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + transactionToUpdate.getAccount().getId() + " not exist in DB");
    }

    Account accountToUpdate = transactionAccountBeforeUpdate.get();
    accountToUpdate
        .setBalance(accountToUpdate.getBalance().add(transactionToUpdate.getPrice()));
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);

    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategory(transaction.getCategory());
    transactionToUpdate.setPrice(transaction.getPrice());
    transactionToUpdate.setAccount(transaction.getAccount());
    transactionToUpdate.setDate(transaction.getDate());

    Optional<Account> transactionAccountAfterUpdate = accountService
        .getAccountById(transactionToUpdate.getAccount().getId());

    if (!transactionAccountAfterUpdate.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + transactionToUpdate.getAccount().getId() + " not exist in DB");
    }

    Account accountToUpdateAfterTransactionUpdate = transactionAccountAfterUpdate.get();
    accountToUpdateAfterTransactionUpdate
        .setBalance(
            accountToUpdateAfterTransactionUpdate.getBalance().subtract(transaction.getPrice()));

    accountService.updateAccount(accountToUpdateAfterTransactionUpdate.getId(),
        accountToUpdateAfterTransactionUpdate);

    transactionRepository.save(transactionToUpdate);
  }

  public void deleteTransaction(long id) {
    Optional<Transaction> transactionToDelete = getTransactionById(id);

    if (!transactionToDelete.isPresent()) {
      throw new IllegalStateException("Transaction with id: " + id + " not exist in DB");
    }

    long transactionAccountId = transactionToDelete.get().getAccount().getId();
    Optional<Account> transactionAccount = accountService
        .getAccountById(transactionAccountId);

    if (!transactionAccount.isPresent()) {
      throw new IllegalStateException(
          "Account with id: " + transactionAccountId + " not exist in DB");
    }
    Account accountToUpdate = transactionAccount.get();
    accountToUpdate
        .setBalance(accountToUpdate.getBalance().add(transactionToDelete.get().getPrice()));
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
    transactionRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return transactionRepository.existsById(id);
  }

}
