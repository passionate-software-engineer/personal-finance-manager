package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.account.Account;
import com.pfm.currency.Currency;
import java.math.BigDecimal;

public class TestAccountProvider {

  public static Account accountJacekBalance1000() {
    return Account.builder()
        .name("Jacek Millenium Bank savings")
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(1L).name("USD").exchangeRate(BigDecimal.valueOf(3.99)).build())
        .build();
  }

  public static Account accountMbankBalance10() {
    return Account.builder()
        .name("Mbank")
        .balance(convertDoubleToBigDecimal(10))
        .currency(Currency.builder().id(2L).name("PLN").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
  }

  public static Account accountMilleniumBalance100() {
    return Account.builder()
        .name("Millenium")
        .balance(convertDoubleToBigDecimal(100))
        .currency(Currency.builder().id(3L).name("EUR").exchangeRate(BigDecimal.valueOf(4.24)).build())
        .build();
  }

  public static Account accountIngBalance9999() {
    return Account.builder()
        .name("Ing")
        .balance(convertDoubleToBigDecimal(9999))
        .currency(Currency.builder().id(4L).name("GBP").exchangeRate(BigDecimal.valueOf(5.99)).build())
        .build();
  }

  public static Account accountIdeaBalance100000() {
    return Account.builder()
        .name("Idea")
        .balance(convertDoubleToBigDecimal(100000))
        .currency(Currency.builder().id(5L).name("AED").exchangeRate(BigDecimal.valueOf(3.29)).build())
        .build();
  }
}