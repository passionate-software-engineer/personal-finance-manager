package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.account.Account;
import java.math.BigDecimal;

public class TestAccountProvider {

  public static final Account ACCOUNT_RAFAL_BALANCE_0 =
      Account.builder()
          .id(Long.MAX_VALUE)
          .name("Rafal ALIOR account")
          .balance(BigDecimal.valueOf(0))
          .build();

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