package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.account.Account;
import com.pfm.account.AccountController.AccountRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestAccountProvider {

  public static final Long MOCK_ACCOUNT_ID = 1L;
  public static final BigDecimal ACCOUNT_BALANCE = BigDecimal.valueOf(100);

  public static final Account ACCOUNT_ANDRZEJ_BALANCE_1_000_000 =
      Account.builder()
          .id(12L)
          .name("Sebastian Revolut USD")
          .balance(BigDecimal.valueOf(1_000_000))
          .build();

  public static final Account ACCOUNT_PIOTR_BALANCE_9 =
      Account.builder()
          .id(444L)
          .name("Cash") // TODO correct all places to rounding
          .balance(BigDecimal.valueOf(9).setScale(2, RoundingMode.HALF_UP))
          .build();

  public static final Account ACCOUNT_SLAWEK_BALANCE_9 =
      Account.builder()
          .id(15L)
          .name("Cash")
          .balance(BigDecimal.valueOf(9))
          .build();

  public static final Account ACCOUNT_JACEK_BALANCE_1000 =
      Account.builder()
          .id(2L)
          .name("Jacek Millenium Bank savings")
          .balance(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
          .build();

  public static final Account ACCOUNT_LUKASZ_BALANCE_1124 =
      Account.builder()
          .id(122L)
          .name("Lukasz CreditBank")
          .balance(BigDecimal.valueOf(1124))
          .build();

  public static final Account ACCOUNT_RAFAL_BALANCE_0 =
      Account.builder()
          .id(Long.MAX_VALUE)
          .name("Adam ALIOR account")
          .balance(BigDecimal.valueOf(0))
          .build();

  public static final Account ACCOUNT_MARIUSZ_BALANCE_200 =
      Account.builder()
          .id(89L)
          .name("Mateusz mBank saving account")
          .balance(BigDecimal.valueOf(200.00))
          .build();

  public static final Account ACCOUNT_MARCIN_BALANCE_10_99 =
      Account.builder()
          .id(17L)
          .name("Jurek BZWBK account")
          .balance(BigDecimal.valueOf(10.99))
          .build();

  public static final AccountRequest ACCOUNT_REQUEST_MBANK_BALANCE_1000 =
      AccountRequest.builder()
          .name("MBank")
          .balance(convertDoubleToBigDecimal(1000))
          .build();

  public static final AccountRequest ACCOUNT_REQUEST_DAMIAN_BALANCE_10 =
      AccountRequest.builder()
          .name("Damian mBank account")
          .balance(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
          .build();

  public static AccountRequest getAccountRequestDamianBalance10() {
    return ACCOUNT_REQUEST_DAMIAN_BALANCE_10;
  }

  public static AccountRequest getAccountRequestMbankBalance1000() {
    return ACCOUNT_REQUEST_MBANK_BALANCE_1000;
  }
}