package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportAccountsStateValidatorTest {

  private ImportAccountsStateValidator importAccountsStateValidator = new ImportAccountsStateValidator();

  @BeforeEach
  void setUp() {
    importAccountsStateValidator = new ImportAccountsStateValidator();
  }

  @ParameterizedTest
  @MethodSource("accountsStateValidate")
  public void shouldReturnErrorLogForMissingData(ExportAccount inputAccount, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setInitialAccountsState(Collections.singletonList(inputAccount));
    input.setFinalAccountsState(Collections.singletonList(inputAccount));

    // when
    List<String> resultForInitialAccountsState = importAccountsStateValidator.validate(input.getInitialAccountsState(),
        "initial accounts state");

    // then
    for (int i = 0; i < resultForInitialAccountsState.size(); i++) {
      assertEquals(expectedMessages.get(i), resultForInitialAccountsState.get(i));
    }
  }

  static Stream<Arguments> accountsStateValidate() {
    return Stream.of(
        Arguments.arguments(missingName(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0 name;")),
        Arguments.arguments(missingAccountType(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0 account type;")),
        Arguments.arguments(missingBalance(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0 balance;")),
        Arguments.arguments(missingCurrency(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0 currency;")),
        Arguments.arguments(missingLastVerification(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0 last verification date;")),
        Arguments.arguments(missingAllData(),
            Collections.singletonList("All incorrect or missing fields in initial accounts state number: 0"
                + " name; account type; balance; currency; last verification date;")));
  }

  private static ExportResult.ExportAccount missingName() {
    ExportResult.ExportAccount exportAccount = correctAccountsState();
    exportAccount.setName("");
    return exportAccount;
  }

  private static ExportResult.ExportAccount missingAccountType() {
    ExportResult.ExportAccount exportAccount = correctAccountsState();
    exportAccount.setAccountType("");
    return exportAccount;
  }

  private static ExportResult.ExportAccount missingBalance() {
    ExportResult.ExportAccount exportAccount = correctAccountsState();
    exportAccount.setBalance(null);
    return exportAccount;
  }

  private static ExportResult.ExportAccount missingCurrency() {
    ExportResult.ExportAccount exportAccount = correctAccountsState();
    exportAccount.setCurrency("");
    return exportAccount;
  }

  private static ExportResult.ExportAccount missingLastVerification() {
    ExportResult.ExportAccount exportAccount = correctAccountsState();
    exportAccount.setLastVerificationDate(null);
    return exportAccount;
  }

  private static ExportResult.ExportAccount missingAllData() {
    return new ExportAccount();
  }

  private static ExportResult.ExportAccount correctAccountsState() {
    return ExportAccount.builder()
        .name("ExampleAccountsState")
        .accountType("ExampleType")
        .archived(false)
        .balance(BigDecimal.valueOf(100))
        .currency("ExampleCurrency")
        .lastVerificationDate(LocalDate.now())
        .build();
  }
}
