package com.pfm.account;

import io.swagger.annotations.ApiModelProperty;
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

  @ApiModelProperty(value = "Account name", required = true, example = "Alior Bank savings account")
  private String name;

  @ApiModelProperty(value = "Account's balance", required = true, example = "1438.89")
  private BigDecimal balance;

  @ApiModelProperty(value = "Account's currency id", required = true, example = "1")
  private long currencyId;

  @ApiModelProperty(value = "Account's type id", required = true, example = "1")
  private long accountTypeId;

  @ApiModelProperty(value = "Account's last verification date", example = "2019-01-31")
  private LocalDate lastVerificationDate;
}
