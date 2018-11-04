package com.pfm.transaction;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static com.pfm.history.DifferenceProvider.UPDATE_ENTRY_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransactionDifferncesProviderTest {

  @Test
  void getDifferencesAllFieldsChangedTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setCategoryId(1L);
    transaction.getAccountPriceEntries().get(0).setAccountId(1L);

    Transaction transactionWithNewValues = foodTransactionWithNoAccountAndNoCategory();
    transactionWithNewValues.setDescription("Food for trip");
    transactionWithNewValues.setCategoryId(2L);
    transactionWithNewValues.getAccountPriceEntries().get(0).setAccountId(2L);
    transactionWithNewValues.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(20));
    transactionWithNewValues.setDate(LocalDate.now());

    //when
    final List<String> differences = transaction.getDifferences(transactionWithNewValues);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", transaction.getDescription(), transactionWithNewValues.getDescription()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date", transaction.getDate().toString(), transactionWithNewValues.getDate().toString()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "category", transaction.getCategoryId(), transactionWithNewValues.getCategoryId()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "account", transaction.getAccountPriceEntries().get(0).getAccountId(),
        transactionWithNewValues.getAccountPriceEntries().get(0).getAccountId()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price", transaction.getAccountPriceEntries().get(0).getPrice(),
        transactionWithNewValues.getAccountPriceEntries().get(0).getPrice()));

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesDeleteAccountPriceEntryTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setCategoryId(1L);
    transaction.getAccountPriceEntries().get(0).setAccountId(1L);
    AccountPriceEntry accountPriceEntry = AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(22))
        .accountId(3L)
        .build();
    transaction.setAccountPriceEntries(Arrays.asList(transaction.getAccountPriceEntries().get(0), accountPriceEntry));

    Transaction transactionWithNewValues = foodTransactionWithNoAccountAndNoCategory();
    transactionWithNewValues.setCategoryId(1L);
    transactionWithNewValues.getAccountPriceEntries().get(0).setAccountId(1L);

    //when
    final List<String> differences = transaction.getDifferences(transactionWithNewValues);

    //then
    List<String> expected = new ArrayList<>();

    expected.add("Account price entry was deleted from transaction. Account: " + transaction.getAccountPriceEntries().get(1).getAccountId()
        + ", price: " + transaction.getAccountPriceEntries().get(1).getPrice());

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesAddAccountPriceEntryTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setCategoryId(1L);
    transaction.getAccountPriceEntries().get(0).setAccountId(1L);

    Transaction transactionWithNewValues = foodTransactionWithNoAccountAndNoCategory();
    transactionWithNewValues.setCategoryId(1L);
    transactionWithNewValues.getAccountPriceEntries().get(0).setAccountId(1L);
    AccountPriceEntry accountPriceEntry = AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(22))
        .accountId(3L)
        .build();
    transactionWithNewValues.setAccountPriceEntries(Arrays.asList(transactionWithNewValues.getAccountPriceEntries().get(0), accountPriceEntry));

    //when
    final List<String> differences = transaction.getDifferences(transactionWithNewValues);

    //then
    List<String> expected = new ArrayList<>();

    expected
        .add("New account price entry was added to transaction. Account: " + transactionWithNewValues.getAccountPriceEntries().get(1).getAccountId()
            + ", price: " + transactionWithNewValues.getAccountPriceEntries().get(1).getPrice());

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesNoChangesTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setCategoryId(1L);
    transaction.getAccountPriceEntries().get(0).setAccountId(1L);

    //when
    final List<String> differences = transaction.getDifferences(transaction);

    //then
    List<String> expected = new ArrayList<>();
    assertThat(differences, equalTo(expected));

  }

  @Test
  void getObjectPropertiesWithValuesTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setCategoryId(1L);
    transaction.getAccountPriceEntries().get(0).setAccountId(1L);

    //when
    final List<String> objectPropertiesWithValues = transaction.getObjectPropertiesWithValues();

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "name", transaction.getDescription()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "date", transaction.getDate()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "category", transaction.getCategoryId()));

    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      expected.add(String.format(ENTRY_VALUES_TEMPLATE, "price", entry.getPrice().toString()));
      expected.add(String.format(ENTRY_VALUES_TEMPLATE, "account", entry.getAccountId()));
    }

    assertThat(objectPropertiesWithValues, equalTo(expected));
  }

  @Test
  void getObjectDescriptiveNameTest() {

    //given
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();

    //when
    assertThat(transaction.getDescription(), equalTo(transaction.getObjectDescriptiveName()));
  }
}