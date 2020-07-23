package com.pfm.transaction;

import com.pfm.transaction.DateHelper.DateRange;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
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

  public Optional<DateRange> getDateRangeFromTransactions(Collection<Transaction> transactions) {

    Collection<LocalDate> dates = getDatesFromTransactions(transactions);
    return DateHelper.getDateRange(dates);
  }

  private Collection<LocalDate> getDatesFromTransactions(Collection<Transaction> transactions) {
    return transactions.stream()
        .map(Transaction::getDate)
        .collect(Collectors.toList());
  }

}
