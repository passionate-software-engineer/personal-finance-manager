package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.Collections;

public class TestTransactionProvider {

  private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);

  public static Transaction convertTransactionToPlannedTransaction(com.pfm.transaction.Transaction transaction) {
    return Transaction.builder()
        .id(transaction.getId())
        .description(transaction.getDescription())
        .categoryId(transaction.getCategoryId())
        .date(FUTURE_DATE)
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .userId(transaction.getUserId())
        .build();
  }

  public static com.pfm.transaction.Transaction foodTransactionWithNoAccountAndNoCategory() {
    return com.pfm.transaction.Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(10))
                .build())
        )
        .description("Food for birthday")
        .date(LocalDate.of(2018, 8, 8))
        .build();
  }

  public static com.pfm.transaction.Transaction carTransactionWithNoAccountAndNoCategory() {
    return com.pfm.transaction.Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(30))
                .build())
        )
        .description("Oil")
        .date(LocalDate.of(2018, 8, 10))
        .build();
  }

  public static com.pfm.transaction.Transaction animalsTransactionWithNoAccountAndNoCategory() {
    return com.pfm.transaction.Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .price(convertDoubleToBigDecimal(8))
                .build())
        )
        .description("Food for Parrot")
        .date(LocalDate.of(2018, 10, 1))
        .build();
  }

  public static com.pfm.transaction.Transaction homeTransactionWithNoAccountAndNoCategory() {
    return com.pfm.transaction.Transaction.builder()
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
