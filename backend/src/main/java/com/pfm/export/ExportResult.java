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
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class ExportResult {

  private ExportFundsSummary sumOfAllFundsAtTheBeginningOfExport;
  private ExportFundsSummary sumOfAllFundsAtTheEndOfExport;

  private List<ExportAccount> initialAccountsState;
  private List<ExportAccount> finalAccountsState;

  private List<ExportCategory> categories;

  private List<ExportPeriod> periods;
  private List<ExportFilter> filters;
  private List<HistoryEntry> historyEntries;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class ExportPeriod {

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
  public static final class ExportCategory {

    private String name;
    private String parentCategoryName;
    @Default
    private int priority = 1000;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class ExportFilter {

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
  public static final class ExportTransaction {

    private String description;

    private String category;

    private LocalDate date;

    @Builder.Default
    private List<ExportAccountPriceEntry> accountPriceEntries = new ArrayList<>();
  }

  @Data
  @Builder(toBuilder = true)
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class ExportAccount {

    private String name;

    private String bankAccountNumber;

    private BigDecimal balance;

    private String currency;

    private String accountType;

    private LocalDate lastVerificationDate;

    private boolean archived;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class ExportAccountPriceEntry {

    private String account;

    private BigDecimal price;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class ExportFundsSummary {

    private Map<String, BigDecimal> currencyToFundsMap;

    private BigDecimal sumOfAllFundsInBaseCurrency;

  }
}
