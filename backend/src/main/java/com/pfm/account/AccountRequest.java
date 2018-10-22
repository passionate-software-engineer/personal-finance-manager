package com.pfm.account;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
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
}
