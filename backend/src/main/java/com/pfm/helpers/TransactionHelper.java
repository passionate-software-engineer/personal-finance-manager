package com.pfm.helpers;

import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;

public class TransactionHelper {

  public static TransactionRequest convertTransactionToTransactionRequest(Transaction transaction) {
    return TransactionRequest.builder()
        .description(transaction.getDescription())
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .date(transaction.getDate())
        .categoryId(transaction.getCategoryId())
        .isPlanned(transaction.isPlanned())
        .build();
  }

}
