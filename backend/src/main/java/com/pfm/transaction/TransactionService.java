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
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional // TODO review all services and add transactions support & tests
  public Transaction addTransaction(Transaction transaction) {
    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      addAmountToAccount(entry.getAccountId(), entry.getPrice());
    }

    return transactionRepository.save(transaction);
  }

  @Transactional
  public void updateTransaction(long id, Transaction transaction) {
    Transaction transactionToUpdate = getTransactionFromDatabase(id);

    // subtract old value
    for (AccountPriceEntry entry : transactionToUpdate.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), entry.getPrice());
    }

    // add new value
    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      addAmountToAccount(entry.getAccountId(), entry.getPrice());
    }

    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategoryId(transaction.getCategoryId());
    transactionToUpdate.setAccountPriceEntries(transaction.getAccountPriceEntries());
    transactionToUpdate.setDate(transaction.getDate());

    transactionRepository.save(transactionToUpdate);
  }

  @Transactional
  public void deleteTransaction(long id) {
    Transaction transactionToDelete = getTransactionFromDatabase(id);

    for (AccountPriceEntry entry : transactionToDelete.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), entry.getPrice());
    }

    transactionRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return transactionRepository.existsById(id);
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

    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
  }

}
