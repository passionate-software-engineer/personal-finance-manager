package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class TransactionRequest {

  @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 5")
  protected String description;

  @ApiModelProperty(value = "CATEGORY id", required = true, example = "1")
  protected Long categoryId;

  @ApiModelProperty(value = "Date", required = true, example = "2018-12-31")
  protected LocalDate date;

  @Builder.Default
  @ApiModelProperty(value = "Price & Account entries")
  private List<AccountPriceEntry> accountPriceEntries = new ArrayList<>();

  @ApiModelProperty(value = "Is transaction planned")
  @JsonProperty("isPlanned")
  private boolean isPlanned;

  private RecurrencePeriod recurrencePeriod;

}
