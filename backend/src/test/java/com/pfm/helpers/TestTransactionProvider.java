package com.pfm.helpers;

import com.pfm.transaction.TransactionRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TestTransactionProvider {

  public static final TransactionRequest FOOD_TRANSACTION_REQUEST_WITH_NO_ACCOUNT_AND_NO_CATEGORY =
      TransactionRequest.builder()
          .date(LocalDate.of(2018,8,8))
          .description("Food for birthday")
          .price(BigDecimal.valueOf(10.00).setScale(2, BigDecimal.ROUND_HALF_UP))
          .build();

  public static TransactionRequest getFoodTransactionRequestWithNoAccountAndNoCategory() {
    return TransactionRequest.builder()
        .price(FOOD_TRANSACTION_REQUEST_WITH_NO_ACCOUNT_AND_NO_CATEGORY.getPrice())
        .description(FOOD_TRANSACTION_REQUEST_WITH_NO_ACCOUNT_AND_NO_CATEGORY.getDescription())
        .date(FOOD_TRANSACTION_REQUEST_WITH_NO_ACCOUNT_AND_NO_CATEGORY.getDate())
        .build();
  }

  public static TransactionRequest getCarTransactionRequestWithNoAccountAndNoCategory() {
    return TransactionRequest.builder()
        .price(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_UP))
        .description("Oil")
        .date(LocalDate.of(2018,8,10))
        .build();
  }

}
