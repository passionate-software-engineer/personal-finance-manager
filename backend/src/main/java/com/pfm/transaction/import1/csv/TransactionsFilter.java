package com.pfm.transaction.import1.csv;

import com.pfm.transaction.Transaction;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionsFilter {

  public Collection<Transaction> discardEntriesWithoutImportId(Collection<Transaction> uniqueTransactions) {
    return uniqueTransactions.stream()
        .filter(transaction -> !transaction.getImportId().isEmpty())
        .collect(Collectors.toList());
  }
}
