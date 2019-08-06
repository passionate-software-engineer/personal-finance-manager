package com.pfm.planned_transaction;

import com.pfm.transaction.AccountPriceEntry;
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
public class PlannedTransactionRequest {

  @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 4")
  protected String description;

  @ApiModelProperty(value = "CATEGORY id", required = true, example = "1")
  protected Long categoryId;

  @ApiModelProperty(value = "Date", required = true, example = "2018-12-31")
  protected LocalDate dueDate;

  @Builder.Default
  @ApiModelProperty(value = "Price & Account entries")
  private List<AccountPriceEntry> accountPriceEntries = new ArrayList<>();

}
