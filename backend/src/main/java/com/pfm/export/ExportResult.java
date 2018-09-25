package com.pfm.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.category.Category;
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
public class ExportResult {

  private List<ExportAccount> initialAccountsState;
  private List<ExportAccount> finalAccountsState;

  private List<ExportPeriod> periods;
  private List<Category> categories;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class ExportPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    private List<ExportAccount> accountStateAtTheBeginingOfPeriod;
    private List<ExportAccount> accountStateAtTheEndOfPeriod;

    private Collection<ExportTransaction> transactions;

    // TODO add sum of all money

  }

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

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true) // TODO remove
  static class ExportAccount {

    private String name;

    private BigDecimal balance;

  }
}
