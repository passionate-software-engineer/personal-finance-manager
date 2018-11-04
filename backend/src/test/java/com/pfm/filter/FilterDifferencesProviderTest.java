package com.pfm.filter;

import static com.pfm.helpers.BigDecimalHelper.convertBigDecimalToString;
import static com.pfm.helpers.TestFilterProvider.convertIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpensesJuly;
import static com.pfm.helpers.TestFilterProvider.filterWithNameOnly;
import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static com.pfm.history.DifferenceProvider.UPDATE_ENTRY_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FilterDifferencesProviderTest {

  @Test
  void getDifferencesAllFieldsChangedTest() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L));
    filter.setAccountIds(convertIdsToList(1L));

    Filter filterWithNewValues = filterFoodExpensesJuly();
    filterWithNewValues.setCategoryIds(convertIdsToList(2L));
    filterWithNewValues.setAccountIds(convertIdsToList(2L));

    //when
    final List<String> differences = filter.getDifferences(filterWithNewValues);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", filter.getName(), filterWithNewValues.getName()));
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "accounts ids", filter.getAccountIds().toString(), filterWithNewValues.getAccountIds().toString()));
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "categories ids", filter.getCategoryIds().toString(),
            filterWithNewValues.getCategoryIds().toString()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", convertBigDecimalToString(filter.getPriceFrom()),
        convertBigDecimalToString(filterWithNewValues.getPriceFrom())));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", convertBigDecimalToString(filter.getPriceTo()),
        convertBigDecimalToString(filterWithNewValues.getPriceTo())));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", filter.getDateFrom(), filterWithNewValues.getDateFrom()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", filter.getDateTo(), filterWithNewValues.getDateTo()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", filter.getDescription(), filterWithNewValues.getDescription()));

    assertThat(differences, equalTo(expected));
  }

  @Test
  void getDifferencesAllPossibleFieldsChangedToNullTest() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L));
    filter.setAccountIds(convertIdsToList(1L));

    Filter filterWithNewValues = filterWithNameOnly();
    filterWithNewValues.setName(filterFoodExpenses().getName());

    //when
    final List<String> differences = filter.getDifferences(filterWithNewValues);

    //then
    List<String> expected = new ArrayList<>();
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "accounts ids", filter.getAccountIds().toString(), filterWithNewValues.getAccountIds().toString()));
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "categories ids", filter.getCategoryIds().toString(),
            filterWithNewValues.getCategoryIds().toString()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", convertBigDecimalToString(filter.getPriceFrom()), "empty"));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", convertBigDecimalToString(filter.getPriceTo()), "empty"));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", filter.getDateFrom(), "empty"));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", filter.getDateTo(), "empty"));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", filter.getDescription(), "empty"));

    assertThat(differences, equalTo(expected));
  }

  @Test
  void getDifferencesAllPossibleFieldsChangedFromNullToValueTest() {

    //given
    Filter filter = filterWithNameOnly();
    filter.setName(filterFoodExpenses().getName());

    Filter filterWithNewValues = filterFoodExpenses();
    filterWithNewValues.setCategoryIds(convertIdsToList(1L));
    filterWithNewValues.setAccountIds(convertIdsToList(1L));

    //when
    final List<String> differences = filter.getDifferences(filterWithNewValues);

    //then
    List<String> expected = new ArrayList<>();
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "accounts ids", filter.getAccountIds().toString(),
            filterWithNewValues.getAccountIds().toString()));
    expected
        .add(String.format(UPDATE_ENTRY_TEMPLATE, "categories ids", filter.getCategoryIds().toString(),
            filterWithNewValues.getCategoryIds().toString()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", "empty", convertBigDecimalToString(filterWithNewValues.getPriceFrom())));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", "empty", convertBigDecimalToString(filterWithNewValues.getPriceTo())));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", "empty", filterWithNewValues.getDateFrom()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", "empty", filterWithNewValues.getDateTo()));
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", "empty", filterWithNewValues.getDescription()));

    assertThat(differences, equalTo(expected));
  }

  @Test
  void getDifferencesNoChangesTest() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setAccountIds(convertIdsToList(1L));
    filter.setCategoryIds(convertIdsToList(1L));

    //when
    List<String> differences = filter.getDifferences(filter);

    //then
    List<String> expected = new ArrayList<>();
    assertThat(differences, equalTo(expected));
  }

  @Test
  void getDifferencesNoChangesWithNameOnlyTest() {

    //given
    Filter filter = filterWithNameOnly();

    //when
    List<String> differences = filter.getDifferences(filter);

    //then
    List<String> expected = new ArrayList<>();
    assertThat(differences, equalTo(expected));
  }

  @Test
  void getObjectPropertiesWithValuesTest() {

    //given
    Filter filter = filterFoodExpenses();
    filter.setCategoryIds(convertIdsToList(1L));
    filter.setAccountIds(convertIdsToList(1L));

    //when
    final List<String> objectPropertiesWithValues = filter.getObjectPropertiesWithValues();

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "name", filter.getName()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "accounts ids", filter.getAccountIds().toString()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "categories ids", filter.getCategoryIds().toString()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "price from", convertBigDecimalToString(filter.getPriceFrom())));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "price to", convertBigDecimalToString(filter.getPriceTo())));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "date from", filter.getDateFrom().toString()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "date to", filter.getDateTo().toString()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "description", filter.getDescription()));

    assertThat(objectPropertiesWithValues, equalTo(expected));

  }

  @Test
  void getObjectDescriptiveNameTest() {

    //given
    Filter filter = filterFoodExpenses();

    //when
    assertThat(filter.getName(), equalTo(filter.getObjectDescriptiveName()));
  }
}