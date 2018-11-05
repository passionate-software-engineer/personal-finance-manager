package com.pfm.history;

import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static com.pfm.history.DifferenceProvider.UPDATE_ENTRY_TEMPLATE;

import com.pfm.account.AccountService;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class HistoryEntryService {

  private static final String ADD_ENTRY_TEMPLATE = "Added %s";
  private static final String DELETE_ENTRY_TEMPLATE = "Deleted %s '%s'";
  private static final String CHANGES_TEMPLATE = "%s '%s' changes";

  private HistoryEntryRepository historyEntryRepository;
  private AccountService accountService;

  public List<HistoryEntry> getHistoryEntries(long userId) {
    return historyEntryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(HistoryEntry::getId))
        .collect(Collectors.toList());
  }

  // TODO refactor security to not force develeopers to pass userId all around.
  public <T extends DifferenceProvider<T>> void addEntryOnAdd(T newObject, long userId) {
    List<String> entries = newObject.getObjectPropertiesWithValues();
    entries.add(0, String.format(ADD_ENTRY_TEMPLATE, newObject.getClass().getSimpleName()));
    if (newObject instanceof Transaction) {
      addTransactionAccountsOnAdd((Transaction) newObject, entries, userId);
    }
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnDelete(T oldObject, long userId) {
    List<String> entries = new ArrayList<>();
    entries.add(String.format(DELETE_ENTRY_TEMPLATE, oldObject.getClass().getSimpleName(), oldObject.getObjectDescriptiveName()));
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnUpdate(T objectToUpdate, T objectWithNewValues, long userId) {
    List<String> differences = objectToUpdate.getDifferences(objectWithNewValues);
    if (objectToUpdate instanceof Transaction) {
      addTransactionAccountsOnUpdate((Transaction) objectToUpdate, (Transaction) objectWithNewValues, differences, userId);
    }
    if (differences.isEmpty()) {
      return;
    }
    differences.add(0, String.format(CHANGES_TEMPLATE, objectToUpdate.getClass().getSimpleName(), objectToUpdate.getObjectDescriptiveName()));
    saveHistoryEntries(differences, userId);
  }

  private void saveHistoryEntries(List<String> entries, long userId) {
    HistoryEntry historyEntry = HistoryEntry.builder()
        .userId(userId)
        .date(LocalDateTime.now())
        .entry(entries)
        .build();
    historyEntryRepository.save(historyEntry);
    System.out.println("dupa");
  }

  private void addTransactionAccountsOnAdd(Transaction newObject, List<String> entries, long userId) {
    for (AccountPriceEntry entry : newObject.getAccountPriceEntries()) {
      entries.add(String.format(ENTRY_VALUES_TEMPLATE, "price", entry.getPrice().toString()));
      entries
          .add(String.format(ENTRY_VALUES_TEMPLATE, "account", accountService.getAccountFromDbByIdAndUserId(entry.getAccountId(), userId).getName()));
    }
  }

  private void addTransactionAccountsOnUpdate(Transaction transactionToUpdate, Transaction transactionWithNewValues, List<String> entries,
      long userId) {

    List<AccountPriceEntry> thisEntries = transactionToUpdate.getAccountPriceEntries();
    List<AccountPriceEntry> otherEntries = transactionWithNewValues.getAccountPriceEntries();

    Iterator<AccountPriceEntry> thisEntriesIterator = thisEntries.iterator();
    Iterator<AccountPriceEntry> otherEntriesIterator = otherEntries.iterator();

    while (thisEntriesIterator.hasNext() && otherEntriesIterator.hasNext()) {
      AccountPriceEntry thisEntry = thisEntriesIterator.next();
      AccountPriceEntry otherEntry = otherEntriesIterator.next();

      if (!(thisEntry.getPrice().compareTo(otherEntry.getPrice()) == 0)) {
        entries.add(String.format(UPDATE_ENTRY_TEMPLATE, "price", thisEntry.getPrice().toString(), otherEntry.getPrice().toString()));
      }

      if (!(thisEntry.getAccountId().equals(otherEntry.getAccountId()))) {
        entries.add(String
            .format(UPDATE_ENTRY_TEMPLATE, "account", accountService.getAccountFromDbByIdAndUserId(thisEntry.getAccountId(), userId).getName(),
                accountService.getAccountFromDbByIdAndUserId(otherEntry.getAccountId(), userId).getName()));
      }
    }

    for (int i = thisEntries.size(); i < otherEntries.size(); ++i) {
      AccountPriceEntry accountPriceEntry = otherEntries.get(i);
      entries.add(
          "New account price entry was added to transaction. Account: " + accountService
              .getAccountFromDbByIdAndUserId(accountPriceEntry.getAccountId(), userId).getName()
              + ", price: " + accountPriceEntry.getPrice());
    }

    for (int i = otherEntries.size(); i < thisEntries.size(); ++i) {
      AccountPriceEntry accountPriceEntry = thisEntries.get(i);
      entries.add(
          "Account price entry was deleted from transaction. Account: " + accountService
              .getAccountFromDbByIdAndUserId(accountPriceEntry.getAccountId(), userId).getName()
              + ", price: " + accountPriceEntry.getPrice());
    }
  }
}

