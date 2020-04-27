package com.pfm.helpers;

import com.pfm.account.type.AccountType;

public class TestAccountTypeProvider {

  public static AccountType accountInvestment() {
    return AccountType.builder()
        .id(1L)
        .name("AccountInvestment")
        .build();
  }
}


