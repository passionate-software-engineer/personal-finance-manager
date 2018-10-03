package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;
import java.time.LocalDate;

public class TestTransactionProvider {

  public static Transaction foodTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .price(convertDoubleToBigDecimal(-10))
        .description("Food for birthday")
        .date(LocalDate.of(2018, 8, 8))
        .build();
  }

  public static TransactionRequest carTransactionRequestWithNoAccountAndNoCategory() {
    return TransactionRequest.builder()
        .price(convertDoubleToBigDecimal(-20))
        .description("Oil")
        .date(LocalDate.of(2018, 8, 10))
        .build();
  }

  public static Transaction carTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .price(convertDoubleToBigDecimal(20))
        .description("Oil")
        .date(LocalDate.of(2018, 8, 10))
        .build();
  }

  public static Transaction animalsTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .price(convertDoubleToBigDecimal(8))
        .description("Food for Parrot")
        .date(LocalDate.of(2018, 10, 1))
        .build();
  }

  public static Transaction homeTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .price(convertDoubleToBigDecimal(77))
        .description("Table")
        .date(LocalDate.of(2018, 10, 2))
        .build();
  }

}
