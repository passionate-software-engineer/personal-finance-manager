package com.pfm.export.converter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionExporterTest {

  private static final long USER_ID = 1L;

  private static final long CATEGORY_ID = 2L;

  private TransactionExporter transactionExporter;

  @Mock
  private TransactionService transactionService;

  @Mock
  private CategoryService categoryService;

  @Mock
  private AccountService accountService;

  private DateFormatter dateFormatter = new TransactionMonthDateFormatter();

  @BeforeEach
  public void setUp() {
    transactionExporter = new TransactionExporter(transactionService,
        categoryService, accountService, dateFormatter);

    when(categoryService.getCategoryByIdAndUserId(CATEGORY_ID, USER_ID)).thenReturn(createCategory());
    when(accountService.getAccountByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
  }

  @Test
  public void shouldCreateMapWithOneMonthAndOneTransaction() {
    // given
    LocalDate date = LocalDate.of(2000, 5, 12);
    when(transactionService.getTransactions(USER_ID)).thenReturn(List.of(createTransaction(date)));

    // when
    Map<String, List<ExportTransaction>> transactionsByMonth = transactionExporter.getTransactionsByMonth(USER_ID);

    // then
    Assertions.assertEquals(transactionsByMonth.size(), 1);
    List<ExportTransaction> transaction = transactionsByMonth.get(dateFormatter.toString(date));
    Assertions.assertEquals(transaction.get(0).getAccountPriceEntries().get(0).getPrice(), BigDecimal.TEN);
  }

  @Test
  public void shouldCreateMapWithTwoMonthsAndOneTransaction() {
    // given
    LocalDate firstMonth = LocalDate.of(2000, 5, 12);
    LocalDate secondMonth = LocalDate.of(2000, 6, 12);
    when(transactionService.getTransactions(USER_ID)).thenReturn(
        List.of(createTransaction(firstMonth), createTransaction(secondMonth)));

    // when
    Map<String, List<ExportTransaction>> transactionsByMonth = transactionExporter.getTransactionsByMonth(USER_ID);

    // then
    Assertions.assertEquals(transactionsByMonth.size(), 2);

    List<ExportTransaction> firstMonthTransactions = transactionsByMonth.get(dateFormatter.toString(firstMonth));
    List<ExportTransaction> secondMonthTransactions = transactionsByMonth.get(dateFormatter.toString(secondMonth));

    Assertions.assertEquals(firstMonthTransactions.get(0).getAccountPriceEntries().size(), 1);
    Assertions.assertEquals(secondMonthTransactions.get(0).getAccountPriceEntries().size(), 1);
  }

  @Test
  public void shouldCreateMapWithOneMonthAndTwoTransactions() {
    // given
    LocalDate month = LocalDate.of(2000, 5, 12);
    when(transactionService.getTransactions(USER_ID)).thenReturn(List.of(createTransaction(month), createTransaction(month)));

    // when
    Map<String, List<ExportTransaction>> transactionsByMonth = transactionExporter.getTransactionsByMonth(USER_ID);

    // then
    Assertions.assertEquals(transactionsByMonth.size(), 1);
    Assertions.assertEquals(transactionsByMonth.get(dateFormatter.toString(month)).size(), 2);
  }

  private Optional<Category> createCategory() {
    return Optional.of(Category
        .builder()
        .name("CATEGORY_NAME")
        .build());
  }

  private Transaction createTransaction(final LocalDate date) {
    return Transaction
        .builder()
        .categoryId(CATEGORY_ID)
        .date(date)
        .accountPriceEntries(List.of(createAccountPriceEntry()))
        .build();
  }

  private AccountPriceEntry createAccountPriceEntry() {
    long exampleAccountId = 5L;

    return AccountPriceEntry
        .builder()
        .price(BigDecimal.TEN)
        .accountId(exampleAccountId)
        .build();
  }
}

