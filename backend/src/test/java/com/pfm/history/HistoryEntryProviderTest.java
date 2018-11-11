package com.pfm.history;

import static com.pfm.helpers.TestAccountProvider.accountIngBalance9999;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.category.CategoryService;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;

@ExtendWith(MockitoExtension.class)
class HistoryEntryProviderTest {

  @Mock
  private AccountService accountService;

  @Mock
  private CategoryService categoryService;

  @Mock
  private UserProvider userProvider;

  @InjectMocks
  private HistoryEntryProvider historyEntryProvider;

  @Test
  void createHistoryEntryOnAdd() {

    //given
    Transaction transaction = getTransaction();
    when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryCar());
    when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    when(userProvider.getCurrentUserId()).thenReturn(1L);

    //when
    final List<HistoryInfo> historyInfos = historyEntryProvider.createHistoryEntryOnAdd(transaction);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .newValue(transaction.getDescription())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryId")
        .newValue(categoryCar().getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("date")
        .newValue(transaction.getDate().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountPriceEntries")
        .newValue(String.format("[%s - %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnUpdate() {

    //given
    Transaction transaction = getTransaction();
    Transaction updatedTransaction = getTransactionWithNewValues();

    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(2L, 1L)).thenReturn(categoryHome());

    lenient().when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    lenient().when(accountService.getAccountFromDbByIdAndUserId(2L, 1L)).thenReturn(accountIngBalance9999());
    when(userProvider.getCurrentUserId()).thenReturn(1L);

    //when
    final List<HistoryInfo> historyInfos = historyEntryProvider.createHistoryEntryOnUpdate(transaction, updatedTransaction);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .oldValue(transaction.getDescription())
        .newValue(updatedTransaction.getDescription())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryId")
        .oldValue(categoryFood().getName())
        .newValue(categoryHome().getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("date")
        .oldValue(transaction.getDate().toString())
        .newValue(updatedTransaction.getDate().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountPriceEntries")
        .oldValue(String.format("[%s - %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .newValue(String.format("[%s - %s]", accountIngBalance9999().getName(), updatedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));

  }

  @Test
  void createHistoryEntryOnDelete() {
    //given
    Transaction transaction = getTransaction();
    when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    when(userProvider.getCurrentUserId()).thenReturn(1L);

    //when
    final List<HistoryInfo> historyInfos = historyEntryProvider.createHistoryEntryOnDelete(transaction);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .oldValue(transaction.getDescription())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryId")
        .oldValue(categoryFood().getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("date")
        .oldValue(transaction.getDate().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountPriceEntries")
        .oldValue(String.format("[%s - %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  private Transaction getTransaction() {
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    List<AccountPriceEntry> accountPriceEntries = new ArrayList<>();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(100.00))
        .accountId(1L)
        .build()
    ));
    transaction.setCategoryId(1L);
    return transaction;
  }

  private Transaction getTransactionWithNewValues() {
    Transaction transaction = carTransactionWithNoAccountAndNoCategory();
    transaction.setDescription("Food for party");
    transaction.setDate(LocalDate.of(2018, 10, 10));

    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(321.00))
        .accountId(2L)
        .build()
    ));
    transaction.setCategoryId(2L);
    return transaction;
  }

  @Test
  public void shouldThrowExceptionInGetValueFromFieldMethod() throws Exception {

    Account account = new Account();
    final Field name = account.getClass().getDeclaredField("name");

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      Whitebox.invokeMethod(historyEntryProvider, "getValueFromField", name, account);
    });
  }

  @Test
  public void shouldReturnNullAndCatchException() throws Exception {
    Account account = new Account();
    Field mock = mock(Field.class);
    when(mock.get(account)).thenThrow(new IllegalAccessException());
    Object value = Whitebox.invokeMethod(historyEntryProvider, "getValue", mock, account);

    assertTrue(value == null);

  }
}