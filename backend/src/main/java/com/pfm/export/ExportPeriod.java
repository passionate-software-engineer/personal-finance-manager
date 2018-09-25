package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportPeriod {

  private LocalDate startDate;
  private LocalDate endDate;

  private List<Account> accountStateAtTheBeginingOfPeriod;
  private List<Account> accountStateAtTheEndOfPeriod;

  private Collection<Transaction> transactions;

  // TODO add sum of all money
}
