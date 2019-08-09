package com.pfm.planned_transaction;

import static com.pfm.helpers.TestTransactionProvider.convertTransactionToPlannedTransaction;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlannedTransactionServiceTest {

  private static final long NOT_EXISTING_ID = 0;
  private static final long MOCK_USER_ID = 1;

  @Mock
  private PlannedTransactionRepository plannedTransactionRepository;

  @InjectMocks
  private PlannedTransactionService plannedTransactionService;

  @Test
  public void shouldReturnExceptionCausedByIdDoesNotExistInDb() {
    //given
    when(plannedTransactionRepository.findByIdAndUserId(NOT_EXISTING_ID, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> plannedTransactionService.deletePlannedTransaction(NOT_EXISTING_ID, MOCK_USER_ID));
    assertThat(exception.getMessage(), is(equalTo("Planned transaction with id: " + NOT_EXISTING_ID + " does not exist in database")));
  }

  @Test
  public void shouldConvertTransactionToPlannedTransaction() {
    Transaction transaction = Transaction.builder()
        .date(LocalDate.now().minusDays(1))
        .description("Fuel")
        .categoryId(2L)
        .userId(1L)
        .accountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
            .id(3L)
            .accountId(7L)
            .price(BigDecimal.valueOf(11.27))
            .build()))
        .build();

    PlannedTransaction expected =
        new PlannedTransaction(null, "Fuel", 2L, LocalDate.now().plusDays(1),
            Collections.singletonList(AccountPriceEntry.builder()
                .id(3L)
                .accountId(7L)
                .price(BigDecimal.valueOf(11.27)).build()), 1L);

    //when
    PlannedTransaction actual = convertTransactionToPlannedTransaction(transaction);

    //then
    assertEquals(expected, actual);

  }

}
