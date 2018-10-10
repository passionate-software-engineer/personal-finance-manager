package com.pfm.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResult {

  private List<ExportAccount> initialAccountsState = new ArrayList<>();
  private List<ExportAccount> finalAccountsState = new ArrayList<>();

  private List<ExportPeriod> periods = new ArrayList<>();
  private List<Category> categories = new ArrayList<>();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class ExportPeriod {

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
  @JsonIgnoreProperties(ignoreUnknown = true) // TODO remove
  static class ExportTransaction {

    private String description;

    private String category;

    private LocalDate date;

    private String account; // TODO remove

    private BigDecimal price; // TODO remove

    private List<ExportAccountPriceEntry> accountPriceEntries = new ArrayList<>();
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static class ExportAccount {

    private String name;

    private BigDecimal balance;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static class ExportAccountPriceEntry {

    private String account;

    private BigDecimal price;
  }
}
