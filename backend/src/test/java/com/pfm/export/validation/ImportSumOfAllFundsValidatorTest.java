package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportFundsSummary;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportSumOfAllFundsValidatorTest {

  private ImportSumOfAllFundsValidator importSumOfAllFundsValidator = new ImportSumOfAllFundsValidator();

  @ParameterizedTest
  @MethodSource("exportSumValidate")
  public void shouldReturnErrorLogForMissingData(ExportResult inputSum, List<String> expectedMessages) {
    // when
    List<String> result = importSumOfAllFundsValidator.validate(inputSum);

    // then
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }

  static Stream<Arguments> exportSumValidate() {
    return Stream.of(
        Arguments.arguments(missingStartSum(),
            Collections.singletonList("Sum of all Funds At The Beginning Of Export is missing")),

        Arguments.arguments(missingEndSum(),
            Collections.singletonList("Sum of all Funds At The End Of Export is missing")));
  }

  private static ExportResult missingStartSum() {

    ExportResult exportResult = correctSums();
    exportResult.setSumOfAllFundsAtTheBeginningOfExport(null);
    return exportResult;
  }

  private static ExportResult missingEndSum() {

    ExportResult exportResult = correctSums();
    exportResult.setSumOfAllFundsAtTheEndOfExport(null);
    return exportResult;
  }

  private static ExportResult correctSums() {
    ExportResult exportResult = new ExportResult();

    exportResult.setSumOfAllFundsAtTheBeginningOfExport(ExportFundsSummary.builder()
        .sumOfAllFundsInBaseCurrency(BigDecimal.valueOf(100))
        .build());

    exportResult.setSumOfAllFundsAtTheEndOfExport(ExportFundsSummary.builder()
        .sumOfAllFundsInBaseCurrency(BigDecimal.valueOf(100))
        .build());

    return exportResult;
  }
}
