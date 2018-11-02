package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.DifferenceProvider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public final class Transaction implements DifferenceProvider<Transaction> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TODO -- add Swagger annotations - type is used in getAllTransactions
  private String description;

  private Long categoryId;

  private LocalDate date;

  @JsonIgnore
  private Long userId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<AccountPriceEntry> accountPriceEntries;

  @Override
  public List<String> getDifferences(Transaction transaction) {
    List<String> differences = new ArrayList<>();

    // TODO add transaction name so it's easy to know which one was updated
    if (!(transaction.getDescription().equals(this.getDescription()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", this.getDescription(), transaction.getDescription()));
    }
    if (!(this.categoryId.equals(transaction.getCategoryId()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "Transaction category", this.categoryId, transaction.getCategoryId()));
    }

    List<AccountPriceEntry> thisEntries = this.getAccountPriceEntries();
    List<AccountPriceEntry> otherEntries = transaction.getAccountPriceEntries();

    Iterator<AccountPriceEntry> thisEntriesIterator = thisEntries.iterator();
    Iterator<AccountPriceEntry> otherEntriesIterator = otherEntries.iterator();

    while (thisEntriesIterator.hasNext() && otherEntriesIterator.hasNext()) {
      AccountPriceEntry thisEntry = thisEntriesIterator.next();
      AccountPriceEntry otherEntry = otherEntriesIterator.next();

      if (!(thisEntry.getAccountId().equals(otherEntry.getAccountId()))) {
        differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "account", thisEntry.getAccountId(), otherEntry.getAccountId()));
      }
      if (!(thisEntry.getPrice().compareTo(otherEntry.getPrice()) == 0)) {
        differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price", thisEntry.getPrice().toString(), otherEntry.getPrice().toString()));
      }
    }

    for (int i = thisEntries.size(); i < otherEntries.size(); ++i) {
      AccountPriceEntry accountPriceEntry = otherEntries.get(i);
      differences.add(
          "New account price entry was added to transaction. Account: " + accountPriceEntry.getAccountId()
              + ", price: " + accountPriceEntry.getPrice());
    }

    for (int i = otherEntries.size(); i < thisEntries.size(); ++i) {
      AccountPriceEntry accountPriceEntry = thisEntries.get(i);
      differences.add(
          "Account price entry was deleted from transaction. Account: " + accountPriceEntry.getAccountId()
              + ", price: " + accountPriceEntry.getPrice());
    }

    if (!(this.getDate().equals(transaction.getDate()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "Transaction date", this.getDate().toString(), transaction.getDate().toString()));
    }

    if (!differences.isEmpty()) {
      differences.add(0, "Transaction '" + this.getDescription() + "' changes");
    }
    return differences;
  }

  @Override
  public List<String> getObjectPropertiesWithValues() {
    List<String> newValues = new ArrayList<>();
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "name", this.getDescription()));
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "date", this.getDate().toString()));
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "category", this.getCategoryId()));
    for (AccountPriceEntry entry : this.getAccountPriceEntries()) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "price", entry.getPrice().toString()));
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "account", entry.getAccountId()));
    }
    return newValues;
  }

  @Override
  public String getObjectDescriptiveName() {
    return this.getDescription();
  }

}