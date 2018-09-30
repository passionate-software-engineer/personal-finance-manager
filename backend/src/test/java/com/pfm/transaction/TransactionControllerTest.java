package com.pfm.transaction;

import static com.pfm.helpers.TestTransactionProvider.carTransactionRequestWithNoAccountAndNoCategory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {

  private long mockUserId = 1;

  @Mock
  private TransactionValidator transactionValidator;

  @Mock
  private CategoryService categoryService;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private TransactionController transactionController;

  //TODO check duplicated code and reasons for this tests

  @Test
  public void shouldReturnExceptionCausedByNotExistingCategoryIdAndNotExistingAccountId() {

    //given
    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.setAccountId(1L);
    transactionRequestToAdd.setCategoryId(1L);

    when(transactionValidator.validate(transactionRequestToAdd, mockUserId)).thenReturn(new ArrayList<>());
    when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd, mockUserId);
    });
    assertThat(exception.getMessage(), is(equalTo("Account or category was not provided")));
  }

  @Test
  public void shouldReturnExceptionCausedByNotExistingCategoryId() {
    //given
    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.setAccountId(1L);
    transactionRequestToAdd.setCategoryId(1L);

    when(transactionValidator.validate(transactionRequestToAdd, mockUserId)).thenReturn(new ArrayList<>());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd, mockUserId);
    });
    assertThat(exception.getMessage(), is(equalTo("Account or category was not provided")));
  }

  @Test
  public void shouldReturnExceptionCausedByNotExistingExistingAccountId() {
    //given
    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.setAccountId(1L);
    transactionRequestToAdd.setCategoryId(1L);

    when(transactionValidator.validate(transactionRequestToAdd, mockUserId)).thenReturn(new ArrayList<>());
    when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd, mockUserId);
    });
    assertThat(exception.getMessage(), is(equalTo("Account or category was not provided")));

  }
}
