package com.pfm.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

  @Schema(description = "Filter Name", example = "Expenses June")
  private String name;

  @Schema(description = "Account ids", example = "[1, 3, 99]")
  private List<Long> accountIds;

  @Schema(description = "Category ids", example = "[1, 3, 99]")
  private List<Long> categoryIds;

  @Schema(description = "Price from", example = "100")
  private BigDecimal priceFrom;

  @Schema(description = "Price to", example = "300")
  private BigDecimal priceTo;

  @Schema(description = "Date", example = "2018-06-15")
  private LocalDate dateFrom;

  @Schema(description = "Date", example = "2018-07-16")
  private LocalDate dateTo;

  @Schema(description = "Description", example = "Food filter")
  private String description;

  private Boolean isDefault;
}
