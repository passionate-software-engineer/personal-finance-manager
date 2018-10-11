package com.pfm.export;

import com.pfm.category.Category;
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

  private List<ExportAccount> initialAccountsState = new ArrayList<>();
  private List<ExportAccount> finalAccountsState = new ArrayList<>();

  private List<ExportPeriod> periods = new ArrayList<>();
  private List<Category> categories = new ArrayList<>();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static final class ExportPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    private List<ExportAccount> accountStateAtTheBeginingOfPeriod = new ArrayList<>();
    private List<ExportAccount> accountStateAtTheEndOfPeriod = new ArrayList<>();

    private Collection<ExportTransaction> transactions = new ArrayList<>();

    // TODO add sum of all money

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static final class ExportTransaction {

    private String description;

    private String category;

    private LocalDate date;

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
