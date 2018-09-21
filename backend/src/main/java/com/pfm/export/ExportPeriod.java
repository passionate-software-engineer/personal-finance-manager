package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportPeriod {

  private LocalDate startDate;
  private LocalDate endDate;
  private List<Account> accountStateAtTheBeginingOfPeriod;
  private List<Account> accountStateAtTheEndOfPeriod;
  private List<Category> categories;
  private Set<Transaction> transactions;
  // TODO add sum of all money
}
