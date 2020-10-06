package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportPeriodsValidatorTest {

  private ImportPeriodsValidator importPeriodsValidator;

  @BeforeEach
  void setUp() {
    ImportAccountsStateValidator importAccountsStateValidator = new ImportAccountsStateValidator();
    importPeriodsValidator = new ImportPeriodsValidator(importAccountsStateValidator);
  }

  @ParameterizedTest
  @MethodSource("periodsValidate")
  public void shouldReturnErrorLogForMissingData(ExportPeriod inputPeriod, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setPeriods(Collections.singletonList(inputPeriod));

    // when
    List<String> result = importPeriodsValidator.validate(input.getPeriods());

    // then
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }

  static Stream<Arguments> periodsValidate() {
    return Stream.of(

        Arguments.arguments(missingStartDate(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 start date;")),
        Arguments.arguments(missingEndDate(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 end date;")),
        Arguments.arguments(missingPeriodAccountName(),
            Collections.singletonList(
                "All incorrect or missing fields in periods number: 0 beginning account number: 0 name; end account number: 0 name;")),
        Arguments.arguments(missingPeriodAccountBalance(),
            Collections.singletonList(
                "All incorrect or missing fields in periods number: 0 beginning account number: 0 balance; end account number: 0 balance;")),
        Arguments.arguments(missingTransactionDate(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 in transaction number: 0 date;")),
        Arguments.arguments(missingTransactionChildAccount(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 in transaction number: 0 in entry number: 0 account;")),
        Arguments.arguments(missingTransactionChildPrice(),
            Collections.singletonList(
                "All incorrect or missing fields in periods number: 0 in transaction number: 0 in entry number: 0 price;")),
        Arguments.arguments(missingTransactionCategory(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 in transaction number: 0 category;")),
        Arguments.arguments(missingTransactionDescription(),
            Collections.singletonList("All incorrect or missing fields in periods number: 0 in transaction number: 0 description;")),
        Arguments.arguments(missingSumOfAllFundsInBaseCurrency(),
            Collections.singletonList(
                "All incorrect or missing fields in periods number: 0 beginning sum of all founds in currency; end sum of all founds in currency;")));
  }

  private static ExportResult.ExportPeriod missingStartDate() {
    ExportPeriod exportPeriod = correctPeriod();
    exportPeriod.setStartDate(null);
    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingEndDate() {
    ExportPeriod exportPeriod = correctPeriod();
    exportPeriod.setEndDate(null);
    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingPeriodAccountName() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportAccount> exportAccount = correctAccountState();

    for (ExportAccount exportAccount1 : exportAccount) {
      exportAccount1.setName("");
    }

    exportPeriod.setAccountStateAtTheBeginningOfPeriod(exportAccount);
    exportPeriod.setAccountStateAtTheEndOfPeriod(exportAccount);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingPeriodAccountBalance() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportAccount> exportAccount = correctAccountState();

    for (ExportAccount exportAccount1 : exportAccount) {
      exportAccount1.setBalance(null);
    }

    exportPeriod.setAccountStateAtTheBeginningOfPeriod(exportAccount);
    exportPeriod.setAccountStateAtTheEndOfPeriod(exportAccount);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingTransactionDate() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportTransaction> exportTransaction = correctTransactions();

    for (ExportTransaction exportTransaction1 : exportTransaction) {
      exportTransaction1.setDate(null);
    }

    exportPeriod.setTransactions(exportTransaction);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingTransactionChildAccount() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportTransaction> exportTransaction = correctTransactions();

    for (ExportTransaction exportTransaction1 : exportTransaction) {
      for (ExportAccountPriceEntry accountPriceEntry : exportTransaction1.getAccountPriceEntries()) {
        accountPriceEntry.setAccount("");
      }
    }

    exportPeriod.setTransactions(exportTransaction);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingTransactionChildPrice() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportTransaction> exportTransaction = correctTransactions();

    for (ExportTransaction exportTransaction1 : exportTransaction) {
      for (ExportAccountPriceEntry accountPriceEntry : exportTransaction1.getAccountPriceEntries()) {
        accountPriceEntry.setPrice(null);
      }
    }

    exportPeriod.setTransactions(exportTransaction);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingTransactionCategory() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportTransaction> exportTransaction = correctTransactions();

    for (ExportTransaction exportTransaction1 : exportTransaction) {
      exportTransaction1.setCategory("");
    }

    exportPeriod.setTransactions(exportTransaction);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingTransactionDescription() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportTransaction> exportTransaction = correctTransactions();

    for (ExportTransaction exportTransaction1 : exportTransaction) {
      exportTransaction1.setDescription("");
    }

    exportPeriod.setTransactions(exportTransaction);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingSumOfAllFundsInBaseCurrency() {
    ExportPeriod exportPeriod = correctPeriod();
    ExportFundsSummary fundsSummary = correctSumOfAllFounds();

    fundsSummary.setSumOfAllFundsInBaseCurrency(null);

    exportPeriod.setSumOfAllFundsAtTheBeginningOfPeriod(fundsSummary);
    exportPeriod.setSumOfAllFundsAtTheEndOfPeriod(fundsSummary);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod correctPeriod() {
    return ExportPeriod.builder()
        .startDate(LocalDate.of(2020, 1, 10))
        .endDate(LocalDate.of(2020, 1, 20))
        .accountStateAtTheBeginningOfPeriod(correctAccountState())
        .transactions(correctTransactions())
        .sumOfAllFundsAtTheBeginningOfPeriod(correctSumOfAllFounds())
        .sumOfAllFundsAtTheEndOfPeriod(correctSumOfAllFounds())
        .build();
  }

  private static List<ExportAccount> correctAccountState() {
    return Collections.singletonList(
        ExportAccount.builder()
            .name("ExampleName")
            .accountType("ExampleAccountType")
            .balance(BigDecimal.valueOf(100))
            .currency("ExampleCurrency")
            .lastVerificationDate(LocalDate.now())
            .build());
  }

  private static List<ExportTransaction> correctTransactions() {
    return Collections.singletonList(
        ExportTransaction.builder()
            .date(LocalDate.of(2020, 4, 10))
            .accountPriceEntries(correctAccountPriceEntry())
            .category("ExampleTransactionCategory")
            .description("ExampleDescription")
            .build()
    );
  }

  private static List<ExportAccountPriceEntry> correctAccountPriceEntry() {
    return Collections.singletonList(
        ExportAccountPriceEntry.builder()
            .account("ExampleAccount")
            .price(BigDecimal.valueOf(100))
            .build()
    );
  }

  private static ExportFundsSummary correctSumOfAllFounds() {
    return ExportFundsSummary.builder()
        .currencyToFundsMap(Collections.singletonMap("$$$", BigDecimal.valueOf(100)))
        .sumOfAllFundsInBaseCurrency(BigDecimal.valueOf(100))
        .build();
  }
}
