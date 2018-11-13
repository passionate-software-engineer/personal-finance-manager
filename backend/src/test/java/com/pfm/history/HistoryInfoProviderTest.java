package com.pfm.history;

import static com.pfm.helpers.TestAccountProvider.accountIngBalance9999;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestFilterProvider.convertIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.filter.Filter;
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

@ExtendWith(MockitoExtension.class)
class HistoryInfoProviderTest {

  private static final long USER_ID = 1L;

  @Mock
  private AccountService accountService;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private HistoryInfoProvider historyInfoProvider;

  @Test
  void createHistoryEntryOnAddForTransaction() {

    //given
    Transaction transaction = getTransaction();
    when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryCar());
    when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnAdd(transaction, USER_ID);

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
        .newValue(String.format("[%s : %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnAddForFilter() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L, 2L));
    filter.setAccountIds(convertIdsToList(1L, 2L));
    filter.setDescription(null);

    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(2L, 1L)).thenReturn(categoryHome());

    lenient().when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    lenient().when(accountService.getAccountFromDbByIdAndUserId(2L, 1L)).thenReturn(accountIngBalance9999());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnAdd(filter, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .newValue(filter.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountIds")
        .newValue("[Mbank, Ing]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryIds")
        .newValue("[Food, Home]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceFrom")
        .newValue(filter.getPriceFrom().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceTo")
        .newValue(filter.getPriceTo().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateFrom")
        .newValue(filter.getDateFrom().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateTo")
        .newValue(filter.getDateTo().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .newValue(null)
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnAddForCategory() {

    //given
    Category category = categoryOil();
    category.setParentCategory(categoryCar());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnAdd(category, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .newValue(category.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("parentCategory")
        .newValue(category.getParentCategory().getName())
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnAddForCategoryWithNoParentCategory() {

    //given
    Category category = categoryCar();

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnAdd(category, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .newValue(category.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("parentCategory")
        .newValue("Main Category")
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnUpdateForCategory() {

    //given
    Category category = categoryOil();
    category.setParentCategory(categoryCar());
    Category updatedCategory = categoryOil();

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnUpdate(category, updatedCategory, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .oldValue(category.getName())
        .newValue(updatedCategory.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("parentCategory")
        .oldValue(category.getParentCategory().getName())
        .newValue("Main Category")
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnUpdateForFilter() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L));
    filter.setAccountIds(convertIdsToList(1L));

    Filter updatedFilter = Filter.builder()
        .name("updatedName")
        .accountIds(convertIdsToList(2L))
        .categoryIds(convertIdsToList(2L))
        .priceTo(convertDoubleToBigDecimal(999))
        .priceFrom(convertDoubleToBigDecimal(1001))
        .dateFrom(LocalDate.of(2006, 10, 10))
        .dateTo(LocalDate.of(2008, 10, 10))
        .description("updatedDescrition")
        .build();

    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(2L, 1L)).thenReturn(categoryHome());

    lenient().when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    lenient().when(accountService.getAccountFromDbByIdAndUserId(2L, 1L)).thenReturn(accountIngBalance9999());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnUpdate(filter, updatedFilter, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .oldValue(filter.getName())
        .newValue(updatedFilter.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountIds")
        .oldValue("[Mbank]")
        .newValue("[Ing]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryIds")
        .oldValue("[Food]")
        .newValue("[Home]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceFrom")
        .oldValue(filter.getPriceFrom().toString())
        .newValue(updatedFilter.getPriceFrom().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceTo")
        .oldValue(filter.getPriceTo().toString())
        .newValue(updatedFilter.getPriceTo().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateFrom")
        .oldValue(filter.getDateFrom().toString())
        .newValue(updatedFilter.getDateFrom().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateTo")
        .oldValue(filter.getDateTo().toString())
        .newValue(updatedFilter.getDateTo().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .oldValue(filter.getDescription())
        .newValue(updatedFilter.getDescription())
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnUpdateForTransaction() {

    //given
    Transaction transaction = getTransaction();
    Transaction updatedTransaction = getTransactionWithNewValues();

    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(2L, 1L)).thenReturn(categoryHome());

    lenient().when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    lenient().when(accountService.getAccountFromDbByIdAndUserId(2L, 1L)).thenReturn(accountIngBalance9999());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnUpdate(transaction, updatedTransaction, USER_ID);

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
        .oldValue(String.format("[%s : %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .newValue(String.format("[%s : %s]", accountIngBalance9999().getName(), updatedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));

  }

  @Test
  void createHistoryEntryOnDeleteForTransaction() {
    //given
    Transaction transaction = getTransaction();
    when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnDelete(transaction, USER_ID);

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
        .oldValue(String.format("[%s : %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  void createHistoryEntryOnDeleteForFilter() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L, 2L));
    filter.setAccountIds(convertIdsToList(1L, 2L));
    filter.setDateTo(null);
    filter.setPriceFrom(null);
    filter.setDescription(null);

    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(1L, 1L)).thenReturn(categoryFood());
    lenient().when(categoryService.getCategoryFromDbByIdAndUserId(2L, 1L)).thenReturn(categoryHome());

    lenient().when(accountService.getAccountFromDbByIdAndUserId(1L, 1L)).thenReturn(accountMbankBalance10());
    lenient().when(accountService.getAccountFromDbByIdAndUserId(2L, 1L)).thenReturn(accountIngBalance9999());

    //when
    final List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryEntryOnDelete(filter, USER_ID);

    //then
    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("name")
        .oldValue(filter.getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountIds")
        .oldValue("[Mbank, Ing]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryIds")
        .oldValue("[Food, Home]")
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceFrom")
        .oldValue(null)
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("priceTo")
        .oldValue(filter.getPriceTo().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateFrom")
        .oldValue(filter.getDateFrom().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("dateTo")
        .oldValue(null)
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .oldValue(null)
        .build());

    assertThat(historyInfos, is(equalTo(expectedHistoryInfos)));
  }

  @Test
  public void shouldThrowExceptionInGetValueFromFieldMethod() {

    Account account = new Account();

    Throwable exception = assertThrows(IllegalStateException.class,
        () -> historyInfoProvider.createHistoryEntryOnAdd(account, USER_ID));

    assertThat(exception.getMessage(), is("Field value is null"));
  }

  @Test
  public void shouldReturnNullAndCatchException() throws Exception {
    Account account = new Account();
    final Field name = account.getClass().getDeclaredField("id");

    assertThrows(RuntimeException.class, () -> historyInfoProvider.getValue(name, account));

  }

  private Transaction getTransaction() {
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
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
}