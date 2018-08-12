package com.pfm.helpers;

import com.pfm.account.Account;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestAccountProvider {

  public static final Long MOCK_ACCOUNT_ID = 1L;

  public static final Account ACCOUNT_SEBASTIAN_BALANCE_1_000_000 =
      Account.builder()
          .id(123L)
          .name("Sebastian Revolut USD")
          .balance(BigDecimal.valueOf(1_000_000))
          .build();

  public static final Account ACCOUNT_PIOTR_BALANCE_9 =
      Account.builder()
          .id(444L)
          .name("Cash")
          .balance(BigDecimal.valueOf(9))
          .build();

  public static final Account ACCOUNT_JACEK_BALANCE_1000 =
      Account.builder()
          .id(666666L)
          .name("Jacek Millenium Bank savings")
          .balance(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
          .build();

  public static final Account ACCOUNT_LUKASZ_BALANCE_1124 =
      Account.builder()
          .id(987654321L)
          .name("Lukasz CreditBank")
          .balance(BigDecimal.valueOf(1124))
          .build();

  public static final Account ACCOUNT_ADAM_BALANCE_0 =
      Account.builder()
          .id(Long.MAX_VALUE)
          .name("Adam ALIOR account")
          .balance(BigDecimal.valueOf(0))
          .build();

  public static final Account ACCOUNT_MATEUSZ_BALANCE_200 =
      Account.builder()
          .id(1L)
          .name("Mateusz mBank saving account")
          .balance(BigDecimal.valueOf(200.00))
          .build();

  public static final Account ACCOUNT_JUREK_BALANCE_10_99 =
      Account.builder()
          .id(0L)
          .name("Jurek BZWBK account")
          .balance(BigDecimal.valueOf(10.99))
          .build();
}