package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.getMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionValidatorTest {

  private static final long NOT_EXISTING_ID = 0L;

  @Mock
  private CategoryService categoryService;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private TransactionValidator transactionValidator;

  @Test
  public void shouldReturnErrorWhenNoTransactionAccountPriceEntriesWereProvidedNullValue() {
    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(null);

    // when
    List<String> result = transactionValidator.validate(transaction, 1);

    // then
    assertThat(result, hasSize(4));
    assertThat(result.get(0), is(getMessage(EMPTY_TRANSACTION_NAME)));
    assertThat(result.get(1), is(getMessage(EMPTY_TRANSACTION_CATEGORY)));
    assertThat(result.get(2), is(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED)));
    assertThat(result.get(3), is(getMessage(EMPTY_TRANSACTION_DATE)));
  }

  @Test
  public void shouldReturnErrorWhenNoTransactionAccountPriceEntriesWereProvidedEmptyList() {
    Transaction transaction = new Transaction();

    // when
    List<String> result = transactionValidator.validate(transaction, 1);

    // then
    assertThat(result, hasSize(4));
    assertThat(result.get(0), is(getMessage(EMPTY_TRANSACTION_NAME)));
    assertThat(result.get(1), is(getMessage(EMPTY_TRANSACTION_CATEGORY)));
    assertThat(result.get(2), is(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED)));
    assertThat(result.get(3), is(getMessage(EMPTY_TRANSACTION_DATE)));
  }

  @Test
  public void shouldReturnErrorWhenNoPriceAndAccountWereProvided() {
    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(new AccountPriceEntry()));
    transaction.setCategoryId(NOT_EXISTING_ID);

    // when
    List<String> result = transactionValidator.validate(transaction, 1);

    // then
    assertThat(result, hasSize(5));
    assertThat(result.get(0), is(getMessage(EMPTY_TRANSACTION_NAME)));
    assertThat(result.get(1), is(String.format(getMessage(CATEGORY_ID_DOES_NOT_EXIST), NOT_EXISTING_ID)));
    assertThat(result.get(2), is(getMessage(EMPTY_TRANSACTION_ACCOUNT)));
    assertThat(result.get(3), is(getMessage(EMPTY_TRANSACTION_PRICE)));
    assertThat(result.get(4), is(getMessage(EMPTY_TRANSACTION_DATE)));
  }

  @Test
  public void shouldReturnErrorWhenAccountIdDoesNotExists() {
    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(new AccountPriceEntry(NOT_EXISTING_ID, null, 1L)));

    when(accountService.accountDoesNotExistByIdAndUserId(eq(NOT_EXISTING_ID), anyLong())).thenReturn(true);

    // when
    List<String> result = transactionValidator.validate(transaction, 1);

    // then
    assertThat(result, hasSize(5));
    assertThat(result.get(0), is(getMessage(EMPTY_TRANSACTION_NAME)));
    assertThat(result.get(1), is(getMessage(EMPTY_TRANSACTION_CATEGORY)));
    assertThat(result.get(2), is(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), NOT_EXISTING_ID)));
    assertThat(result.get(3), is(getMessage(EMPTY_TRANSACTION_PRICE)));
    assertThat(result.get(4), is(getMessage(EMPTY_TRANSACTION_DATE)));
  }

}
