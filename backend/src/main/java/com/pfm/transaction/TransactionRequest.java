package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

  @Setter
  @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 5")
  protected String description;

  @Setter
  @ApiModelProperty(value = "Category id", required = true, example = "1")
  protected Long categoryId;

  @Setter
  @ApiModelProperty(value = "Date", required = true, example = "2018-12-31")
  protected LocalDate date;

  @ApiModelProperty(value = "Price & Account entries")
  private List<AccountPriceEntry> accountPriceEntries;

}