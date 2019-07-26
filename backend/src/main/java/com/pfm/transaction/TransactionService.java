package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.history.HistoryEntryService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

  private AccountPriceEntriesRepository accountPriceEntriesRepository;
  private TransactionRepository transactionRepository;
  private AccountService accountService;
  private HistoryEntryService historyEntryService;

  public Optional<Transaction> getTransactionByIdAndUserId(long id, long userId) {
    return transactionRepository.findByIdAndUserId(id, userId);
  }

  public List<Transaction> getTransactions(long userId) {
    return transactionRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(Transaction::getId))
        .collect(Collectors.toList());
  }

  @Transactional
  public Transaction addTransaction(long userId, Transaction transaction, boolean useDateProvidedInRequest) {
    transaction.setUserId(userId);
    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), useDateProvidedInRequest);
    }

    return transactionRepository.save(transaction);
  }

  @Transactional
  public void updateTransaction(long id, long userId, Transaction transaction) {
    Transaction transactionToUpdate = getTransactionFromDatabase(id, userId);

    // subtract old value
    for (AccountPriceEntry entry : transactionToUpdate.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
    }

    // add new value
    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), false);
    }

    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategoryId(transaction.getCategoryId());
    transactionToUpdate.getAccountPriceEntries().clear();
    transactionToUpdate.getAccountPriceEntries().addAll(transaction.getAccountPriceEntries());
    transactionToUpdate.setDate(transaction.getDate());

    transactionRepository.save(transactionToUpdate);
  }

  @Transactional
  public void deleteTransaction(long id, long userId) {
    Transaction transactionToDelete = getTransactionFromDatabase(id, userId);

    for (AccountPriceEntry entry : transactionToDelete.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
    }

    transactionRepository.deleteById(id);
  }

  private Transaction getTransactionFromDatabase(long id, long userId) {
    Optional<Transaction> transactionFromDb = getTransactionByIdAndUserId(id, userId);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException("Transaction with id: " + id + " does not exist in database");
    }

    return transactionFromDb.get();
  }

  private void subtractAmountFromAccount(long accountId, long userId, BigDecimal amountToAdd) {
    updateAccountBalance(accountId, userId, amountToAdd, BigDecimal::subtract, false);
  }

  private void addAmountToAccount(long accountId, long userId, BigDecimal amountToSubtract, boolean useDateProvidedInRequest) {
    updateAccountBalance(accountId, userId, amountToSubtract, BigDecimal::add, useDateProvidedInRequest);
  }

  private void updateAccountBalance(long accountId, long userId, BigDecimal amount, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation,
      boolean useDateProvidedInRequest) {
    Account account = accountService.getAccountFromDbByIdAndUserId(accountId, userId);

    BigDecimal newBalance = operation.apply(account.getBalance(), amount);

    Account accountWithNewBalance = Account.builder()
        .name(account.getName())
        .balance(newBalance)
        .currency(account.getCurrency())
        .build();

    if (!useDateProvidedInRequest) {
      historyEntryService.addHistoryEntryOnUpdate(account, accountWithNewBalance, userId);
    }

    accountService.updateAccount(accountId, userId, accountWithNewBalance);
  }

  public boolean transactionExistByAccountId(long accountId) {
    return accountPriceEntriesRepository.existsByAccountId(accountId);
  }

  public boolean transactionExistByCategoryId(long categoryId) {
    return transactionRepository.existsByCategoryId(categoryId);
  }

}
