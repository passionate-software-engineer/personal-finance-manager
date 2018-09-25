package com.pfm.export;

import com.pfm.account.Account;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
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

  private Collection<ExportTransaction> transactions;

  // TODO add sum of all money

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static class ExportTransaction {

    private String description;

    private String category;

    private String account;

    private BigDecimal price;

    private LocalDate date;
  }
}
