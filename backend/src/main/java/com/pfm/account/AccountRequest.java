package com.pfm.account;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AccountRequest {

  @Schema(description = "Account name", required = true, example = "Alior Bank savings account")
  private String name;

  @Schema(description = "Account's balance", required = true, example = "1438.89")
  private BigDecimal balance;

  @Schema(description = "Account's currency id", required = true, example = "1")
  private long currencyId;

  @Schema(description = "Account's type id", required = true, example = "1")
  private long accountTypeId;

  @Schema(description = "Account's last verification date", example = "2019-01-31")
  private LocalDate lastVerificationDate;
}
