package com.pfm.account.type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AccountTypeRequest {

  //  @ApiModelProperty(value = "Account type name", required = true, example = "Credit")
  private String name;

}
