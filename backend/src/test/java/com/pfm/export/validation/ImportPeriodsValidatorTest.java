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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportPeriodsValidatorTest {

  private ImportPeriodsValidator importPeriodsValidator = new ImportPeriodsValidator();

  @ParameterizedTest
  @MethodSource("periodsValidate")
  public void shouldReturnErrorLogForMissingData(ExportPeriod inputPeriod, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setPeriods(Collections.singletonList(inputPeriod));

    // when
    List<String> result = importPeriodsValidator.validate(input.getPeriods());

    // then
    for (String errorLog : result) {
      System.out.println(errorLog);
    }
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }

  static Stream<Arguments> periodsValidate() {
    return Stream.of(
        Arguments.arguments(missingStartDate(),
            Collections.singletonList("Period has missing start or end date")),

        Arguments.arguments(missingEndDate(),
            Collections.singletonList("Period has missing start or end date")),

        Arguments.arguments(missingPeriodAccountName(),
            Arrays.asList("Account name is missing in beginning of period from 2020-01-10 to 2020-01-20",
                "Account name is missing in end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingPeriodAccountType(),
            Arrays.asList("ExampleName account has missing type in beginning of period from 2020-01-10 to 2020-01-20",
                "ExampleName account has missing type in end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingPeriodAccountBalance(),
            Arrays.asList("ExampleName account has missing balance in beginning of period from 2020-01-10 to 2020-01-20",
                "ExampleName account has missing balance in end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingPeriodAccountCurrency(),
            Arrays.asList("ExampleName account has missing currency in beginning of period from 2020-01-10 to 2020-01-20",
                "ExampleName account has missing currency in end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingPeriodAccountLastVerificationDate(),
            Arrays.asList("ExampleName account has missing last verification date in beginning of period from 2020-01-10 to 2020-01-20",
                "ExampleName account has missing last verification date in end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingTransactionDate(),
            Collections.singletonList("Transaction has missing date for period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingTransactionChildAccount(),
            Collections.singletonList("Transaction at: 2020-04-10 has missing account for period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingTransactionChildPrice(),
            Collections.singletonList("Transaction at: 2020-04-10 has missing price for period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingTransactionCategory(),
            Collections.singletonList("Transaction at: 2020-04-10 has missing category for period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingTransactionDescription(),
            Collections.singletonList("Transaction at: 2020-04-10 has missing description for period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingCurrencyToFoundsMap(),
            Arrays.asList("Currency founds missing in the beginning of period from 2020-01-10 to 2020-01-20",
                "Currency founds missing in the end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingSumOfAllFundsInBaseCurrency(),
            Arrays.asList("Sum of all founds missing in the beginning of period from 2020-01-10 to 2020-01-20",
                "Sum of all founds missing in the end of period from 2020-01-10 to 2020-01-20")),

        Arguments.arguments(missingAllData(),
            Collections.singletonList("Period has missing start or end date")));
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

  private static ExportResult.ExportPeriod missingPeriodAccountType() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportAccount> exportAccount = correctAccountState();

    for (ExportAccount exportAccount1 : exportAccount) {
      exportAccount1.setAccountType("");
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

  private static ExportResult.ExportPeriod missingPeriodAccountCurrency() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportAccount> exportAccount = correctAccountState();

    for (ExportAccount exportAccount1 : exportAccount) {
      exportAccount1.setCurrency("");
    }

    exportPeriod.setAccountStateAtTheBeginningOfPeriod(exportAccount);
    exportPeriod.setAccountStateAtTheEndOfPeriod(exportAccount);

    return exportPeriod;
  }

  private static ExportResult.ExportPeriod missingPeriodAccountLastVerificationDate() {
    ExportPeriod exportPeriod = correctPeriod();
    List<ExportAccount> exportAccount = correctAccountState();

    for (ExportAccount exportAccount1 : exportAccount) {
      exportAccount1.setLastVerificationDate(null);
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

  private static ExportResult.ExportPeriod missingCurrencyToFoundsMap() {
    ExportPeriod exportPeriod = correctPeriod();
    ExportFundsSummary fundsSummary = correctSumOfAllFounds();

    fundsSummary.setCurrencyToFundsMap(null);

    exportPeriod.setSumOfAllFundsAtTheBeginningOfPeriod(fundsSummary);
    exportPeriod.setSumOfAllFundsAtTheEndOfPeriod(fundsSummary);

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

  private static ExportResult.ExportPeriod missingAllData() {
    return new ExportPeriod();
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
