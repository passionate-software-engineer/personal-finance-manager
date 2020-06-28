package com.pfm.helpers;

import com.pfm.account.type.AccountType;

public class TestAccountTypeProvider {

  public static AccountType accountTypeInvestment() {
    return AccountType.builder()
        .id(1L)
        .name("AccountInvestment")
        .build();
  }

  public static AccountType accountTypeCredit() {
    return AccountType.builder()
        .id(2L)
        .name("AccountCredit")
        .build();
  }
}


