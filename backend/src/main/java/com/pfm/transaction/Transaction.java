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

    if (!(transaction.getDescription().equals(this.getDescription()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", this.getDescription(), transaction.getDescription()));
    }

    if (!(this.getDate().equals(transaction.getDate()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date", this.getDate().toString(), transaction.getDate().toString()));
    }

    if (!(this.categoryId.equals(transaction.getCategoryId()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "category", this.categoryId, transaction.getCategoryId()));
    }




    return differences;
  }

  @Override
  public List<String> getObjectPropertiesWithValues() {
    List<String> newValues = new ArrayList<>();
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "name", this.getDescription()));
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "date", this.getDate().toString()));
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "category", this.getCategoryId()));

    return newValues;
  }

  @Override
  public String getObjectDescriptiveName() {
    return this.getDescription();
  }

}