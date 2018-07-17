package com.pfm.helpers;

import com.pfm.account.Account;

import java.math.BigDecimal;

public class TestAccountProvider {

  public static final Account ACCOUNT_SEBASTIAN_BALANCE_1000
      = Account.builder()
      .name("Sebastian mBank saving account")
      .balance(BigDecimal.valueOf(1000))
      .build();

  public static final Account ACCOUNT_PIOTREK_BALANCE_9
      = Account.builder()
      .name("Piotrek ing saving account")
      .balance(BigDecimal.valueOf(9))
      .build();

  public static final Account ACCOUNT_JACEK_BALANCE_1000
      = Account.builder()
      .name("Jacek mBank saving account")
      .balance(BigDecimal.valueOf(1000))
      .build();

  public static Account ACCOUNT_LUKASZ_BALANCE_1000
      = Account.builder()
      .name("Lukasz mBank saving account")
      .balance(BigDecimal.valueOf(1000))
      .build();

  public static Account ACCOUNT_ADAM_BALANCE_1000
      = Account.builder()
      .name("Adam bzwbk saving account")
      .balance(BigDecimal.valueOf(1000)).build();

  public static Account ACCOUNT_MATEUSZ_BALANCE_200
      = Account.builder()
      .name("Mateusz mBank saving account")
      .balance(BigDecimal.valueOf(200.00)).build();

  public static Account ACCOUNT_JUREK_BALANCE_1000
      = Account.builder()
      .name("Jurek bzwbk saving account")
      .balance(BigDecimal.valueOf(1000))
      .build();
}