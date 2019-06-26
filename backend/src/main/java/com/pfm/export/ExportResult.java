package com.pfm.export;

import com.pfm.history.HistoryEntry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class ExportResult {

  private ExportFundsSummary sumOfAllFundsAtTheBeginningOfExport;
  private ExportFundsSummary sumOfAllFundsAtTheEndOfExport;

  private List<ExportAccount> initialAccountsState = new ArrayList<>();
  private List<ExportAccount> finalAccountsState = new ArrayList<>();

  private List<ExportCategory> categories = new ArrayList<>();

  private List<ExportPeriod> periods = new ArrayList<>();
  private List<ExportFilter> filters = new ArrayList<>();
  private List<HistoryEntry> exportHistoryEntries = new ArrayList<>();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static final class ExportPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder.Default
    private List<ExportAccount> accountStateAtTheBeginningOfPeriod = new ArrayList<>();

    @Builder.Default
    private List<ExportAccount> accountStateAtTheEndOfPeriod = new ArrayList<>();

    @Builder.Default
    private Collection<ExportTransaction> transactions = new ArrayList<>();

    private ExportFundsSummary sumOfAllFundsAtTheBeginningOfPeriod;
    private ExportFundsSummary sumOfAllFundsAtTheEndOfPeriod;

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
  static final class ExportFilter {

    private String name;

    private List<String> accounts;

    private List<String> categories;

    private BigDecimal priceFrom;

    private BigDecimal priceTo;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private String description;

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

    private String currency;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportAccountPriceEntry {

    private String account;

    private BigDecimal price;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportFundsSummary {

    private Map<String, BigDecimal> currencyToFundsMap;

    private BigDecimal sumOfAllFundsInBaseCurrency;

  }
}
