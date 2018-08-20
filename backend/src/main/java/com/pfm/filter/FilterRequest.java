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

  @ApiModelProperty(value = "Filter Name", example = "Food Biedronka")
  private String name;

  private List<Long> accountsIds;

  private List<Long> categoryIds;

  private BigDecimal priceFrom;
  private BigDecimal priceTo;

  @ApiModelProperty(value = "Date", example = "2018-06-15")
  private LocalDate dateFrom;

  @ApiModelProperty(value = "Date", example = "2018-07-16")
  private LocalDate dateTo;

  private String description;
}
