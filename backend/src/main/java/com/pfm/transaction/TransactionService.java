package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
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

  public Optional<Transaction> getTransactionByIdAndUserId(long id, long userId) {
    return transactionRepository.findByIdAndUserId(id, userId);
  }

  public List<Transaction> getTransactions(long userId) {
    return StreamSupport.stream(transactionRepository.findByUserId(userId).spliterator(), false)
        .sorted(Comparator.comparing(Transaction::getId))
        .collect(Collectors.toList());
  }

  public Transaction addTransaction(Transaction transaction) {
    addAmountToAccount(transaction.getAccountId(), transaction.getPrice());
    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!
    return transactionRepository.save(transaction);
  }

  public void updateTransaction(long id, Transaction transaction) {
    Transaction transactionToUpdate = getTransactionFromDatabase(id);

    subtractAmountFromAccount(transactionToUpdate.getAccountId(), transactionToUpdate.getPrice());

    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategoryId(transaction.getCategoryId());
    transactionToUpdate.setPrice(transaction.getPrice());
    transactionToUpdate.setAccountId(transaction.getAccountId());
    transactionToUpdate.setDate(transaction.getDate());

    transactionRepository.save(transactionToUpdate);

    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!

    addAmountToAccount(transactionToUpdate.getAccountId(), transactionToUpdate.getPrice());
  }

  public void deleteTransaction(long id) {
    Transaction transactionToDelete = getTransactionFromDatabase(id);
    transactionRepository.deleteById(id);

    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!
    subtractAmountFromAccount(transactionToDelete.getAccountId(), transactionToDelete.getPrice());
  }

  private Transaction getTransactionFromDatabase(long id) {
    Optional<Transaction> transactionFromDb = getTransactionById(id);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException("Transaction with id: " + id + " does not exist in database");
    }

    return transactionFromDb.get();
  }

  private void subtractAmountFromAccount(long accountId, BigDecimal amountToAdd) {
    updateAccountBalance(accountId, amountToAdd, BigDecimal::subtract);
  }

  private void addAmountToAccount(long accountId, BigDecimal amountToSubtract) {
    updateAccountBalance(accountId, amountToSubtract, BigDecimal::add);
  }

  private void updateAccountBalance(long accountId, BigDecimal amount, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
    Optional<Account> account = accountService.getAccountById(accountId);

    if (!account.isPresent()) {
      throw new IllegalStateException("Account with id: " + accountId + " does not exist in database");
    }

    Account accountToUpdate = account.get();
    // TODO maybe you can write query which updates only balance? that's common operation so does not make sense to update other values
    // I can
    accountToUpdate.setBalance(operation.apply(accountToUpdate.getBalance(), amount));

    //set user id only to run application for testing account api
    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
  }

}
