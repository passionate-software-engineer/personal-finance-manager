package com.pfm.transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionsHelper {

  public TransactionRequest convertTransactionToTransactionRequest(Transaction transaction) {
    return TransactionRequest.builder()
        .description(transaction.getDescription())
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .date(transaction.getDate())
        .categoryId(transaction.getCategoryId())
        .isPlanned(transaction.isPlanned())
        .recurrencePeriod(transaction.getRecurrencePeriod())
        .build();
  }

  public Transaction convertTransactionRequestToTransaction(TransactionRequest transactionRequest) {
    return Transaction.builder()
        .description(transactionRequest.getDescription())
        .categoryId(transactionRequest.getCategoryId())
        .date(transactionRequest.getDate())
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .isPlanned(transactionRequest.isPlanned())
        .recurrencePeriod(transactionRequest.getRecurrencePeriod())
        .build();
  }

}
