package com.pfm.account;

import static com.pfm.helpers.BigDecimalHelper.convertBigDecimalToString;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static com.pfm.history.DifferenceProvider.UPDATE_ENTRY_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountDifferenceProviderTest {

  private Account account = Account.builder()
      .name("Mbank")
      .balance(convertDoubleToBigDecimal(10))
      .build();

  private Account accountWithChanges = Account.builder()
      .name("Ing")
      .balance(convertDoubleToBigDecimal(150))
      .build();

  @Test
  void getDifferencesTest() {

    //when
    List<String> differences = account.getDifferences(accountWithChanges);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", account.getName(), accountWithChanges.getName()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "balance", convertBigDecimalToString(account.getBalance()),
        convertBigDecimalToString(accountWithChanges.getBalance())));

    assertThat(differences, equalTo(expected));
  }

  @Test
  void getObjectPropertiesWithValuesTest() {

    //when
    List<String> outputObjectPropertiesWithValues = account.getObjectPropertiesWithValues();

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "name", account.getName()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "balance", convertBigDecimalToString(account.getBalance())));

    assertThat(outputObjectPropertiesWithValues, equalTo(expected));
  }

  @Test
  void getObjectDescriptiveNameTest() {

    //when
    String output = account.getObjectDescriptiveName();

    //then
    assertThat(output, equalTo(account.getName()));

  }
}