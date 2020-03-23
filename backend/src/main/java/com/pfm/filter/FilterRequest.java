package com.pfm.filter;

import io.swagger.annotations.ApiModelProperty;
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

  @ApiModelProperty(value = "Filter Name", example = "Expenses June")
  private String name;

  //TODO add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> accountIds;

  //TODO add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> categoryIds;

  @ApiModelProperty(value = "Price from", example = "100")
  private BigDecimal priceFrom;

  @ApiModelProperty(value = "Price to", example = "300")
  private BigDecimal priceTo;

  @ApiModelProperty(value = "Date", example = "2018-06-15")
  private LocalDate dateFrom;

  @ApiModelProperty(value = "Date", example = "2018-07-16")
  private LocalDate dateTo;

  @ApiModelProperty(value = "Description", example = "Food filter")
  private String description;

  private Boolean isDefault;
}
