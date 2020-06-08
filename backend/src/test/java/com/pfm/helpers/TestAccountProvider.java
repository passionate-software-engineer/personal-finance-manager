package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.account.Account;
import com.pfm.account.type.AccountType;
import com.pfm.currency.Currency;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TestAccountProvider {

  public static Account accountJacekBalance1000() {
    return Account.builder()
        .name("Jacek Millenium Bank savings")
        .type(AccountType.builder().id(1L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(1L).name("USD").exchangeRate(BigDecimal.valueOf(3.99)).build())
        .lastVerificationDate(LocalDate.now())
        .build();
  }

  public static Account accountMbankBalance10() {
    return Account.builder()
        .name("Mbank")
        .type(AccountType.builder().id(1L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(10))
        .currency(Currency.builder().id(2L).name("PLN").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .lastVerificationDate(LocalDate.now())
        .build();
  }

  public static Account accountMilleniumBalance100() {
    return Account.builder()
        .name("Millenium")
        .type(AccountType.builder().id(1L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(100))
        .currency(Currency.builder().id(3L).name("EUR").exchangeRate(BigDecimal.valueOf(4.24)).build())
        .lastVerificationDate(LocalDate.now())
        .build();
  }

  public static Account accountIngBalance9999() {
    return Account.builder()
        .name("Ing")
        .type(AccountType.builder().id(1L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(9999))
        .currency(Currency.builder().id(4L).name("GBP").exchangeRate(BigDecimal.valueOf(5.99)).build())
        .lastVerificationDate(LocalDate.now())
        .build();
  }

  public static Account accountIdeaBalance100000() {
    return Account.builder()
        .name("Idea")
        .type(AccountType.builder().id(1L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(100000))
        .currency(Currency.builder().id(5L).name("AED").exchangeRate(BigDecimal.valueOf(3.29)).build())
        .lastVerificationDate(LocalDate.now())
        .build();
  }
}
