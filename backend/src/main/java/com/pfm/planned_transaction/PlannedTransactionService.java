package com.pfm.planned_transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.AccountPriceEntriesRepository;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
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
public class PlannedTransactionService {

  private AccountPriceEntriesRepository accountPriceEntriesRepository;
  private PlannedTransactionRepository plannedTransactionRepository;
  private AccountService accountService;
  private HistoryEntryService historyEntryService;

  public Optional<PlannedTransaction> getPlannedTransactionByIdAndUserId(long id, long userId) {
    return plannedTransactionRepository.findByIdAndUserId(id, userId);
  }

  public List<PlannedTransaction> getPlannedTransactions(long userId) {
    return plannedTransactionRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(PlannedTransaction::getId))
        .collect(Collectors.toList());
  }

  @Transactional
  public PlannedTransaction addPlannedTransaction(long userId, PlannedTransaction plannedTransaction, boolean addHistoryEntryOnUpdate) {
    plannedTransaction.setUserId(userId);
    // do not need to modyfy accounts , maybe projected accounts ?
    //    for (AccountPriceEntry entry : plannedTransaction.getAccountPriceEntries()) {
    //    addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), addHistoryEntryOnUpdate);
    //    }

    return plannedTransactionRepository.save(plannedTransaction);
  }

  @Transactional
  public void updatePlannedTransaction(long id, long userId, PlannedTransaction plannedTransaction) {
    PlannedTransaction plannedTransactionToUpdate = getPlannedTransactionFromDatabase(id, userId);

    // subtract old value
    for (AccountPriceEntry entry : plannedTransactionToUpdate.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
    }

    // add new value
    for (AccountPriceEntry entry : plannedTransaction.getAccountPriceEntries()) {
      addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), false);
    }

    plannedTransactionToUpdate.setDescription(plannedTransaction.getDescription());
    plannedTransactionToUpdate.setCategoryId(plannedTransaction.getCategoryId());
    plannedTransactionToUpdate.getAccountPriceEntries().clear();
    plannedTransactionToUpdate.getAccountPriceEntries().addAll(plannedTransaction.getAccountPriceEntries());
    plannedTransactionToUpdate.setDate(plannedTransaction.getDate());

    plannedTransactionRepository.save(plannedTransactionToUpdate);
  }

  @Transactional
  public void deletePlannedTransaction(long id, long userId) {
    Transaction transactionToDelete = getPlannedTransactionFromDatabase(id, userId);

    for (AccountPriceEntry entry : transactionToDelete.getAccountPriceEntries()) {
      subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
    }

    plannedTransactionRepository.deleteById(id);
  }

  private PlannedTransaction getPlannedTransactionFromDatabase(long id, long userId) {
    Optional<PlannedTransaction> plannedTransactionFromDb = getPlannedTransactionByIdAndUserId(id, userId);

    if (!plannedTransactionFromDb.isPresent()) {
      throw new IllegalStateException("Planned transaction with id: " + id + " does not exist in database");
    }

    return plannedTransactionFromDb.get();
  }

  private void subtractAmountFromAccount(long accountId, long userId, BigDecimal amountToAdd) {
    updateAccountBalance(accountId, userId, amountToAdd, BigDecimal::subtract, false);
  }

  private void addAmountToAccount(long accountId, long userId, BigDecimal amountToSubtract, boolean addHistoryEntryOnUpdate) {
    updateAccountBalance(accountId, userId, amountToSubtract, BigDecimal::add, addHistoryEntryOnUpdate);
  }

  private void updateAccountBalance(long accountId, long userId, BigDecimal amount, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation,
      boolean addHistoryEntryOnUpdate) {
    Account account = accountService.getAccountFromDbByIdAndUserId(accountId, userId);

    BigDecimal newBalance = operation.apply(account.getBalance(), amount);

    Account accountWithNewBalance = Account.builder()
        .name(account.getName())
        .balance(newBalance)
        .currency(account.getCurrency())
        .build();

    if (!addHistoryEntryOnUpdate) {
      historyEntryService.addHistoryEntryOnUpdate(account, accountWithNewBalance, userId);
    }

    accountService.updateAccount(accountId, userId, accountWithNewBalance);
  }

  public boolean transactionExistByAccountId(long accountId) {
    return accountPriceEntriesRepository.existsByAccountId(accountId);
  }

  public boolean transactionExistByCategoryId(long categoryId) {
    return plannedTransactionRepository.existsByCategoryId(categoryId);
  }

}
