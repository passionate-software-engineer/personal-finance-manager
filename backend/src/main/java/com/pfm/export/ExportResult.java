package com.pfm.export;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
final class ExportResult {

  private BigDecimal sumOfAllFundsAtTheBeginningOfExport;
  private BigDecimal sumOfAllFundsAtTheEndOfExport;

  private List<ExportAccount> initialAccountsState = new ArrayList<>();
  private List<ExportAccount> finalAccountsState = new ArrayList<>();

  private List<ExportCategory> categories = new ArrayList<>();

  private List<ExportPeriod> periods = new ArrayList<>();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static final class ExportPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder.Default
    private List<ExportAccount> accountStateAtTheBeginingOfPeriod = new ArrayList<>();

    @Builder.Default
    private List<ExportAccount> accountStateAtTheEndOfPeriod = new ArrayList<>();

    @Builder.Default
    private Collection<ExportTransaction> transactions = new ArrayList<>();

    private BigDecimal sumOfAllFundsAtTheBeginningOfPeriod;
    private BigDecimal sumOfAllFundsAtTheEndOfPeriod;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static final class ExportCategory {

    private String name;
    private String parentCategoryName;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportTransaction {

    private String description;

    private String category;

    private LocalDate date;

    @Builder.Default
    private List<ExportAccountPriceEntry> accountPriceEntries = new ArrayList<>();
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportAccount {

    private String name;

    private BigDecimal balance;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportAccountPriceEntry {

    private String account;

    private BigDecimal price;
  }
}
