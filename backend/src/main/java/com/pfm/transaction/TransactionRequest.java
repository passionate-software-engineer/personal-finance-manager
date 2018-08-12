package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

  @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 5")
  protected String description;

  @ApiModelProperty(value = "Category id", required = true, example = "1")
  protected Long categoryId;

  @ApiModelProperty(value = "Account id", required = true, example = "1")
  protected Long accountId;

  @ApiModelProperty(value = "Price", required = true, example = "15.99")
  protected BigDecimal price;

  @ApiModelProperty(value = "Date", required = true, example = "2018-12-31")
  protected LocalDate date;

}