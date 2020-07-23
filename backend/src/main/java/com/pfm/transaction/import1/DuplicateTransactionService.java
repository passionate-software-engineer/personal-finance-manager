package com.pfm.transaction.import1;

import com.pfm.transaction.DateHelper.DateRange;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DuplicateTransactionService {

  private final TransactionRepository transactionRepository;

  public Collection<Transaction> discardTransactionsWithImportIdsAlreadyPresentInDb(Collection<Transaction> transactionsToCheckAgainstDb, long userId,
      Optional<DateRange> dateRangeOptional) {
    List<Transaction> uniqueTransactions = new ArrayList<>();
    final Set<String> importIdsFromDateRange;
    if (dateRangeOptional.isPresent()) {
      final DateRange dateRange = dateRangeOptional.get();
      importIdsFromDateRange = transactionRepository.getImportIdsForDateRange(userId, dateRange.getFromDate(), dateRange.getToDate());
    } else {
      importIdsFromDateRange = transactionRepository.getAllImportIds(userId);
    }

    for (Transaction transaction : transactionsToCheckAgainstDb) {
      if (importIdsFromDateRange.contains(transaction.getImportId())) {
        continue;
      }
      uniqueTransactions.add(transaction);
    }
    return uniqueTransactions;
  }
}
