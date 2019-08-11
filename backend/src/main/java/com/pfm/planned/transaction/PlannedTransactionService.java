package com.pfm.planned.transaction;

import com.pfm.account.AccountService;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.AccountPriceEntriesRepository;
import com.pfm.transaction.AccountPriceEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    // do not need to modify accounts , maybe projected accounts ?
    //    for (AccountPriceEntry entry : plannedTransaction.getAccountPriceEntries()) {
    //    addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), addHistoryEntryOnUpdate);
    //    }

    return plannedTransactionRepository.save(plannedTransaction);
  }

  @Transactional
  public void updatePlannedTransaction(long id, long userId, PlannedTransaction plannedTransaction) {
    PlannedTransaction plannedTransactionToUpdate = getPlannedTransactionFromDatabase(id, userId);

    // subtract old value
    //    for (AccountPriceEntry entry : plannedTransactionToUpdate.getAccountPriceEntries()) {
    //      subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
    //    }

    // add new value
    //    for (AccountPriceEntry entry : plannedTransaction.getAccountPriceEntries()) {
    //      addAmountToAccount(entry.getAccountId(), userId, entry.getPrice(), false);
    //    }

    plannedTransactionToUpdate.setDescription(plannedTransaction.getDescription());
    plannedTransactionToUpdate.setCategoryId(plannedTransaction.getCategoryId());
    plannedTransactionToUpdate.getAccountPriceEntries().clear();
    plannedTransactionToUpdate.getAccountPriceEntries().addAll(plannedTransaction.getAccountPriceEntries());
    plannedTransactionToUpdate.setDate(plannedTransaction.getDate());

    plannedTransactionRepository.save(plannedTransactionToUpdate);
  }

  @Transactional
  public void deletePlannedTransaction(long id, long userId) {
    PlannedTransaction plannedTransactionToDelete = getPlannedTransactionFromDatabase(id, userId);

    for (AccountPriceEntry entry : plannedTransactionToDelete.getAccountPriceEntries()) {
    //          subtractAmountFromAccount(entry.getAccountId(), userId, entry.getPrice());
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

  public boolean transactionExistByAccountId(long accountId) {
    return accountPriceEntriesRepository.existsByAccountId(accountId);
  }

  public boolean transactionExistByCategoryId(long categoryId) {
    return plannedTransactionRepository.existsByCategoryId(categoryId);
  }

}
