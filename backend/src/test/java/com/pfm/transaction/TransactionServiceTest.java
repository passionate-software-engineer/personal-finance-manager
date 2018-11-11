package com.pfm.transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  private static final long NOT_EXISTING_ID = 0;
  private static final long MOCK_USER_ID = 1;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  public void shouldReturnExceptionCausedByIdDoesNotExistInDb() {

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> transactionService.deleteTransaction(NOT_EXISTING_ID, MOCK_USER_ID));
    assertThat(exception.getMessage(), is(equalTo("Transaction with id: " + NOT_EXISTING_ID + " does not exist in database")));
  }

  //  @Test
  //  public void shouldReturnExceptionCausedByAccountIdDoesNotExistInDb() {
  //    //given
  //    Transaction transaction = Transaction.builder()
  //        .accountPriceEntries(Collections.singletonList(
  //            AccountPriceEntry.builder().id(null).accountId(NOT_EXISTING_ID).build()
  //        ))
  //        .build();
  //
  //    when(transactionRepository.findByIdAndUserId(1L, MOCK_USER_ID)).thenReturn(Optional.of(transaction));
  //
  //
  //    //when
  //    Throwable exception = assertThrows(IllegalStateException.class, () -> transactionService.updateTransaction(1L, MOCK_USER_ID, transaction));
  //
  //    assertThat(exception.getMessage(), is(equalTo("Account with id: " + NOT_EXISTING_ID + " does not exist in database")));
  //  }

}