package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;
import java.time.LocalDate;
import java.util.Collections;

public class TestTransactionProvider {

  public static Transaction foodTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(10))
                .build())
        )
        .description("Food for birthday")
        .date(LocalDate.of(2018, 8, 8))
        .build();
  }

  public static TransactionRequest carTransactionRequestWithNoAccountAndNoCategory() {
    return TransactionRequest.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(20))
                .build())
        )
        .description("Oil")
        .date(LocalDate.of(2018, 8, 10))
        .build();
  }

  public static Transaction carTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(30))
                .build())
        )
        .description("Oil")
        .date(LocalDate.of(2018, 8, 10))
        .build();
  }

  public static Transaction animalsTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(8))
                .build())
        )
        .description("Food for Parrot")
        .date(LocalDate.of(2018, 10, 1))
        .build();
  }

  public static Transaction homeTransactionWithNoAccountAndNoCategory() {
    return Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(77))
                .build())
        )
        .description("Table")
        .date(LocalDate.of(2018, 10, 2))
        .build();
  }

}
