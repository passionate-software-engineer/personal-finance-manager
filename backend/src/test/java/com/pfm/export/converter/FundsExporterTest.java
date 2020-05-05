package com.pfm.export.converter;

import static org.mockito.Mockito.when;

import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportFundsSummary;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundsExporterTest {

  private static final long USER_ID = 1L;

  private static final String PLN_CURRENCY = "PLN";

  private static final String USD_CURRENCY = "USD";

  @Mock
  private CurrencyService currencyService;

  @InjectMocks
  private FundsExporter fundsExporter;

  @Test
  public void shouldReturnTotalBalanceEqualTo0() {
    // given
    List<ExportAccount> exportAccounts = List.of(createExportAccount(PLN_CURRENCY, BigDecimal.ZERO));
    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getSumOfAllFundsInBaseCurrency(), BigDecimal.ZERO);
  }

  @Test
  public void shouldReturnTotalBalanceEqualTo1() {
    // given
    List<ExportAccount> exportAccounts = List.of(createExportAccount(PLN_CURRENCY, BigDecimal.ONE));
    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getSumOfAllFundsInBaseCurrency(), BigDecimal.ONE);
  }

  @Test
  public void shouldReturnTotalBalanceEqualTo5() {
    // given
    List<ExportAccount> exportAccounts = List
        .of(createExportAccount(PLN_CURRENCY, BigDecimal.ONE), createExportAccount(USD_CURRENCY, BigDecimal.ONE));
    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE), createCurrency(USD_CURRENCY, BigDecimal.valueOf(4)));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getSumOfAllFundsInBaseCurrency(), BigDecimal.valueOf(5));
  }

  @Test
  public void shouldReturnMapWithAllUsersCurrenciesWithBalance0() {
    // given
    List<ExportAccount> exportAccounts = Collections.emptyList();
    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getCurrencyToFundsMap().size(), 1);
    Assertions.assertEquals(export.getCurrencyToFundsMap().get(PLN_CURRENCY), BigDecimal.ZERO);
  }

  @Test
  public void shouldReturnCurrenciesMapWithCorrectBalance() {
    // given
    List<ExportAccount> exportAccounts = List.of(
        createExportAccount(PLN_CURRENCY, BigDecimal.valueOf(2)),
        createExportAccount(PLN_CURRENCY, BigDecimal.valueOf(3)),
        createExportAccount(USD_CURRENCY, BigDecimal.valueOf(2)),
        createExportAccount(USD_CURRENCY, BigDecimal.valueOf(4)));

    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE),
        createCurrency(USD_CURRENCY, BigDecimal.valueOf(4)));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getCurrencyToFundsMap().get(PLN_CURRENCY), BigDecimal.valueOf(5));
    Assertions.assertEquals(export.getCurrencyToFundsMap().get(USD_CURRENCY), BigDecimal.valueOf(6));
  }

  @Test
  public void shouldCalculateCorrectBalanceForAccountsWithFloatingPointStates() {
    // given
    List<ExportAccount> exportAccounts = List.of(
        createExportAccount(USD_CURRENCY, BigDecimal.valueOf(2.456)),
        createExportAccount(USD_CURRENCY, BigDecimal.valueOf(3.123)));

    List<Currency> currencies = List.of(createCurrency(PLN_CURRENCY, BigDecimal.ONE),
        createCurrency(USD_CURRENCY, BigDecimal.valueOf(4.127)));

    when(currencyService.getCurrencies(USER_ID)).thenReturn(currencies);

    // when
    ExportFundsSummary export = fundsExporter.export(exportAccounts, USER_ID);

    // then
    Assertions.assertEquals(export.getSumOfAllFundsInBaseCurrency(), BigDecimal.valueOf(23.024533));
    Assertions.assertEquals(export.getCurrencyToFundsMap().get(USD_CURRENCY), BigDecimal.valueOf(5.579));
  }

  private ExportAccount createExportAccount(String currency, BigDecimal balance) {
    return ExportAccount
        .builder()
        .balance(balance)
        .currency(currency)
        .build();
  }

  private Currency createCurrency(String currency, BigDecimal exchangeRate) {
    return Currency
        .builder()
        .name(currency)
        .exchangeRate(exchangeRate)
        .build();
  }

}
