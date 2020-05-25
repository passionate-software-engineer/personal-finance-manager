package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportAccountsStateValidatorTest {

  private ImportAccountsStateValidator importAccountsStateValidator = new ImportAccountsStateValidator();

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
    List<String> resultForFinalAccountsState = importAccountsStateValidator.validate(input.getFinalAccountsState(),
        "final accounts state");

    // then
    for (int i = 0; i < resultForInitialAccountsState.size(); i++) {
      assertEquals(expectedMessages.get(i) + "initial accounts state", resultForInitialAccountsState.get(i));
    }

    for (int i = 0; i < resultForFinalAccountsState.size(); i++) {
      assertEquals(expectedMessages.get(i) + "final accounts state", resultForFinalAccountsState.get(i));
    }
  }

  static Stream<Arguments> accountsStateValidate() {
    return Stream.of(
        Arguments.arguments(missingName(),
            Collections.singletonList("Account name is missing in ")),

        Arguments.arguments(missingAccountType(),
            Collections.singletonList("ExampleAccountsState account has missing type in ")),

        Arguments.arguments(missingBalance(),
            Collections.singletonList("ExampleAccountsState account has missing balance in ")),

        Arguments.arguments(missingCurrency(),
            Collections.singletonList("ExampleAccountsState account has missing currency in ")),

        Arguments.arguments(missingLastVerification(),
            Collections.singletonList("ExampleAccountsState account has missing last verification date in ")),

        Arguments.arguments(missingAllData(),
            Collections.singletonList("Account name is missing in ")),

        Arguments.arguments(onlyName(),
            Arrays.asList("ExampleAccountsState account has missing type in ",
                "ExampleAccountsState account has missing balance in ",
                "ExampleAccountsState account has missing currency in ",
                "ExampleAccountsState account has missing last verification date in "))
    );
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

  private static ExportResult.ExportAccount onlyName() {
    ExportResult.ExportAccount exportAccount = new ExportAccount();
    exportAccount.setName("ExampleAccountsState");
    return exportAccount;
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
