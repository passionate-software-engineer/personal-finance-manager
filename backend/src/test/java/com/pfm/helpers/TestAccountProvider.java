package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

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

  public static Account accountMilleniumBalance100() {
    return Account.builder()
        .name("Millenium")
        .balance(convertDoubleToBigDecimal(100))
        .build();
  }

  public static Account accountIngBalance9999() {
    return Account.builder()
        .name("Ing")
        .balance(convertDoubleToBigDecimal(9999))
        .build();
  }

  public static Account accountIdeaBalance100000() {
    return Account.builder()
        .name("Idea")
        .balance(convertDoubleToBigDecimal(100000))
        .build();
  }
}