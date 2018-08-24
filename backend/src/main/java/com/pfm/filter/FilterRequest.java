package com.pfm.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterRequest {

  //TODO check how to add list as example

  @ApiModelProperty(value = "Filter Name", example = "Expenses June")
  private String name;

  private List<Long> accountIds;

  private List<Long> categoryIds;

  @ApiModelProperty(value = "Price from", example = "100")
  private BigDecimal priceFrom;

  @ApiModelProperty(value = "Price to", example = "300")
  private BigDecimal priceTo;

  @ApiModelProperty(value = "Date", example = "2018-06-15")
  private LocalDate dateFrom;

  @ApiModelProperty(value = "Date", example = "2018-07-16")
  private LocalDate dateTo;

  @ApiModelProperty(value = "Description", example = "Food")
  private String description;
}
