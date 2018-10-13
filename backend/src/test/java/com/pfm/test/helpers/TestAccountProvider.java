package com.pfm.test.helpers;

import static com.pfm.test.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.account.Account;

public class TestAccountProvider {

  public static Account accountJacekBalance1000() {
    return Account.builder()
        .name("Jacek Millenium Bank savings")
        .balance(convertDoubleToBigDecimal(1000))
        .build();
  }

  public static Account accountMbankBalance10() {
    return Account.builder()
        .name("Mbank")
        .balance(convertDoubleToBigDecimal(10))
        .build();
  }
}