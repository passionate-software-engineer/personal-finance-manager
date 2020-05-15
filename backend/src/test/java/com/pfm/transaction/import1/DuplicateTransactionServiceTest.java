package com.pfm.transaction.import1;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.helpers.TestTransactionProvider;
import com.pfm.transaction.DateHelper.DateRange;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DuplicateTransactionServiceTest {

  public static final long MOCK_USER_ID = 836349L;
  public static final String MOCK_TRANSACTION_IMPORT_ID = "353324424";

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private DuplicateTransactionService duplicateTransactionService;

  @Test
  void shouldGetAllImportsIdsForMissingDateRange() {
    // given
    Transaction transaction = TestTransactionProvider.animalsTransactionWithNoAccountAndNoCategory();
    transaction.setImportId(MOCK_TRANSACTION_IMPORT_ID);

    List<Transaction> transactions = List.of(transaction);
    Optional<DateRange> dateRangeOptional = Optional.empty();

    when(transactionRepository.getAllImportIds(MOCK_USER_ID)).thenReturn(Set.of(MOCK_TRANSACTION_IMPORT_ID));

    // when
    duplicateTransactionService.discardTransactionsWithImportIdsAlreadyPresentInDb(transactions, MOCK_USER_ID, dateRangeOptional);

    // then
    verify(transactionRepository, times(1)).getAllImportIds(MOCK_USER_ID);
  }

}
