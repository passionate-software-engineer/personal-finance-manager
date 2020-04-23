package com.pfm.export.converter;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodExporterTest {

  private static final long USER_ID = 1L;
  private static final String ACCOUNT_NAME = "ACCOUNT_NAME";

  @Mock
  private FundsExporter fundsExporter;

  @Mock
  private AccountExporter accountExporter;

  @Mock
  private TransactionExporter transactionExporter;

  private DateFormatter dateFormatter = new TransactionMonthDateFormatter();

  private PeriodExporter periodExporter;

  @BeforeEach
  public void setUp() {
    periodExporter = new PeriodExporter(fundsExporter, accountExporter, transactionExporter, dateFormatter);
    when(fundsExporter.export(anyList(), Mockito.eq(USER_ID))).thenReturn(new ExportFundsSummary());
  }

  @Test
  public void shouldCalculateAccountStateForOnePeriod() {
    // given
    when(transactionExporter.getTransactionsByMonth(USER_ID))
        .thenReturn(Map.of(dateFormatter.toString(LocalDate.now()), List.of(createTransactionWithPriceOne(ACCOUNT_NAME))));
    when(accountExporter.export(USER_ID)).thenReturn(List.of(createAccount(BigDecimal.ONE, "ACCOUNT_NAME")));

    // when
    List<ExportPeriod> export = periodExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 1);
    ExportPeriod exportPeriod = export.get(0);
    Assertions.assertEquals(exportPeriod.getTransactions().size(), 1);
    Assertions.assertEquals(exportPeriod.getAccountStateAtTheBeginningOfPeriod().get(0).getBalance(), BigDecimal.ZERO);
    Assertions.assertEquals(exportPeriod.getAccountStateAtTheEndOfPeriod().get(0).getBalance(), BigDecimal.ONE);
  }

  @Test
  public void shouldCalculateAccountStateOfTwoTransactionsForOnePeriod() {
    // given
    when(transactionExporter.getTransactionsByMonth(USER_ID)).thenReturn(Map.of(dateFormatter.toString(LocalDate.now()),
        List.of(createTransactionWithPriceOne(ACCOUNT_NAME), createTransactionWithPriceOne(ACCOUNT_NAME))));
    when(accountExporter.export(USER_ID)).thenReturn(List.of(createAccount(BigDecimal.TEN, "ACCOUNT_NAME")));

    // when
    List<ExportPeriod> export = periodExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 1);
    ExportPeriod exportPeriod = export.get(0);
    Assertions.assertEquals(exportPeriod.getTransactions().size(), 2);
    Assertions.assertEquals(exportPeriod.getAccountStateAtTheBeginningOfPeriod().get(0).getBalance(), BigDecimal.valueOf(8));
    Assertions.assertEquals(exportPeriod.getAccountStateAtTheEndOfPeriod().get(0).getBalance(), BigDecimal.TEN);
  }

  @Test
  public void shouldCalculateAccountStateForTwoPeriods() {
    // given
    when(transactionExporter.getTransactionsByMonth(USER_ID)).thenReturn(Map.of(
        dateFormatter.toString(LocalDate.now()), List.of(createTransactionWithPriceOne(ACCOUNT_NAME)),
        dateFormatter.toString(LocalDate.now().minusMonths(1)), List.of(createTransactionWithPriceOne(ACCOUNT_NAME))));

    when(accountExporter.export(USER_ID)).thenReturn(List.of(createAccount(BigDecimal.TEN, ACCOUNT_NAME)));

    // when
    List<ExportPeriod> export = periodExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 2);
    ExportPeriod firstPeriod = export.get(0);
    ExportPeriod secondPeriod = export.get(1);

    Assertions.assertEquals(firstPeriod.getTransactions().size(), 1);
    Assertions.assertEquals(secondPeriod.getTransactions().size(), 1);

    Assertions.assertEquals(firstPeriod.getAccountStateAtTheEndOfPeriod().get(0).getBalance(), BigDecimal.TEN);
    Assertions.assertEquals(firstPeriod.getAccountStateAtTheBeginningOfPeriod().get(0).getBalance(), BigDecimal.valueOf(9));

    Assertions.assertEquals(secondPeriod.getAccountStateAtTheEndOfPeriod().get(0).getBalance(), BigDecimal.valueOf(9));
    Assertions.assertEquals(secondPeriod.getAccountStateAtTheBeginningOfPeriod().get(0).getBalance(), BigDecimal.valueOf(8));
  }

  @Test
  public void shouldCalculatePeriodForDifferentAccounts() {
    // given
    when(transactionExporter.getTransactionsByMonth(USER_ID)).thenReturn(Map.of(dateFormatter.toString(LocalDate.now()), List.of(
        createTransactionWithPriceOne("FIRST_ACCOUNT"),
        createTransactionWithPriceOne("SECOND_ACCOUNT"))));

    when(accountExporter.export(USER_ID)).thenReturn(List.of(
        createAccount(BigDecimal.TEN, "FIRST_ACCOUNT"),
        createAccount(BigDecimal.ONE, "SECOND_ACCOUNT")));

    // when
    List<ExportPeriod> export = periodExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 1);
    ExportPeriod exportPeriod = export.get(0);
    Assertions.assertEquals(exportPeriod.getTransactions().size(), 2);
    Assertions.assertEquals(accountStateAtTheBeginningWithName(exportPeriod, "FIRST_ACCOUNT").getBalance(), BigDecimal.valueOf(9));
    Assertions.assertEquals(accountStateAtTheEndWithName(exportPeriod, "FIRST_ACCOUNT").getBalance(), BigDecimal.TEN);

    Assertions.assertEquals(accountStateAtTheBeginningWithName(exportPeriod, "SECOND_ACCOUNT").getBalance(), BigDecimal.valueOf(0));
    Assertions.assertEquals(accountStateAtTheEndWithName(exportPeriod, "SECOND_ACCOUNT").getBalance(), BigDecimal.ONE);
  }

  private ExportAccount accountStateAtTheBeginningWithName(ExportPeriod exportPeriod, String accountName) {
    return exportPeriod.getAccountStateAtTheBeginningOfPeriod().stream().filter(exportAccount -> exportAccount.getName().equals(accountName))
        .findFirst().get();
  }

  private ExportAccount accountStateAtTheEndWithName(ExportPeriod exportPeriod, String accountName) {
    return exportPeriod.getAccountStateAtTheEndOfPeriod().stream().filter(exportAccount -> exportAccount.getName().equals(accountName))
        .findFirst().get();
  }

  private ExportAccount createAccount(BigDecimal balance, String accountName) {
    return ExportAccount.builder()
        .balance(balance)
        .name(accountName)
        .build();
  }

  private ExportTransaction createTransactionWithPriceOne(String accountName) {
    return ExportTransaction.builder()
        .accountPriceEntries(List.of(createAccountPriceEntry(accountName)))
        .build();
  }

  private ExportAccountPriceEntry createAccountPriceEntry(String accountName) {
    return ExportAccountPriceEntry.builder()
        .price(BigDecimal.ONE)
        .account(accountName)
        .build();
  }
}
