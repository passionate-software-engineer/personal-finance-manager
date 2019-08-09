package com.pfm.planned_transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
//@AllArgsConstructor
public class PlannedTransaction extends Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String description;

  private Long categoryId;

  LocalDate dueDate;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<AccountPriceEntry> accountPriceEntries;

  @JsonIgnore
  private Long userId;

  public PlannedTransaction(Long id, String description, Long categoryId, LocalDate date,
      List<AccountPriceEntry> accountPriceEntries, Long userId) {
    super(id, description, categoryId, date, accountPriceEntries, userId);
    this.id = id;
    this.description = description;
    this.accountPriceEntries = accountPriceEntries;
    this.categoryId = categoryId;

    this.dueDate = date;
  }

  public PlannedTransaction(String description, Long categoryId, LocalDate date,
      List<AccountPriceEntry> accountPriceEntries) {
    super(description, categoryId, accountPriceEntries);
    this.description = description;
    this.accountPriceEntries = accountPriceEntries;
    this.categoryId = categoryId;

    this.dueDate = date;
  }

  public PlannedTransaction(List<AccountPriceEntry> accountPriceEntries, String description, LocalDate date) {
    super(accountPriceEntries, description);
    this.dueDate = date;
  }

}
