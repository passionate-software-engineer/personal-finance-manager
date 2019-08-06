package com.pfm.planned_transaction;

import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class PlannedTransaction extends Transaction {

  LocalDate dueDate;

  public PlannedTransaction(Long id, String description, Long categoryId, LocalDate date,
      List<AccountPriceEntry> accountPriceEntries, Long userId) {
    super(id, description, categoryId, date, accountPriceEntries, userId);
    this.dueDate = date;
  }

  public PlannedTransaction(String description, Long categoryId, LocalDate date,
      List<AccountPriceEntry> accountPriceEntries) {
    super(description, categoryId, accountPriceEntries);
    this.dueDate = date;
  }

}
