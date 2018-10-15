package com.pfm.transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.account.AccountService;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

  private static final long NOT_EXISTING_ID = 0;
  private static long mockUserId = 1;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  public void shouldReturnExceptionCausedByIdDoesNotExistInDb() throws Exception {
    //given
    when(transactionRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());
    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionService.deleteTransaction(NOT_EXISTING_ID);
    });
    assertThat(exception.getMessage(), is(equalTo("Transaction with id: " + NOT_EXISTING_ID + " does not exist in database")));
  }

  @Test
  public void shouldReturnExceptionCausedByAccountIdDoesNotExistInDb() throws Exception {
    //given
    Transaction transaction = Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder().id(null).accountId(NOT_EXISTING_ID).build()
        ))
        .build();

    when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
    when(accountService.getAccountById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionService.updateTransaction(1L, transaction);
    });

    assertThat(exception.getMessage(), is(equalTo("Account with id: " + NOT_EXISTING_ID + " does not exist in database")));
  }

}
