package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "Description", required = true, example = "Cinema - Star Wars 5")
  protected String description;

  @Schema(description = "CATEGORY id", required = true, example = "1")
  protected Long categoryId;

  @Schema(description = "Date", required = true, example = "2018-12-31")
  protected LocalDate date;

  @Builder.Default
  @Schema(description = "Price & Account entries")
  private List<AccountPriceEntry> accountPriceEntries = new ArrayList<>();

  @Schema(description = "Is transaction planned")
  @JsonProperty("isPlanned")
  private boolean isPlanned;

  private RecurrencePeriod recurrencePeriod;

}
