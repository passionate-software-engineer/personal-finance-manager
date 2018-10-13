package com.pfm.test.helpers;

import static com.pfm.test.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.filter.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFilterProvider {

  private static final LocalDate DATE_OF_1ST_MARCH_2018 = LocalDate.of(2018, 3, 1);
  private static final LocalDate DATE_OF_31ST_MARCH_2018 = LocalDate.of(2018, 3, 31);
  private static final LocalDate DATE_OF_1ST_JANUARY_2018 = LocalDate.of(2018, 1, 1);
  private static final LocalDate DATE_OF_31ST_DECEMBER_2018 = LocalDate.of(2018, 12, 31);

  public static Filter filterFoodExpenses() {
    return Filter.builder()
        .name("Food expenses")
        .dateFrom(DATE_OF_1ST_MARCH_2018)
        .dateTo(DATE_OF_31ST_MARCH_2018)
        .priceFrom(convertDoubleToBigDecimal(100))
        .priceTo(convertDoubleToBigDecimal(300))
        .description("Food")
        .categoryIds(new ArrayList<>())
        .accountIds(new ArrayList<>())
        .build();
  }

  public static Filter filterCarExpenses() {
    return Filter.builder()
        .name("Car expenses")
        .dateFrom(DATE_OF_1ST_JANUARY_2018)
        .dateTo(DATE_OF_31ST_DECEMBER_2018)
        .categoryIds(new ArrayList<>())
        .accountIds(new ArrayList<>())
        .build();
  }

  public static Filter filterHomeExpensesUpTo200() {
    return Filter.builder()
        .name("Home expenses up to 200$")
        .priceTo(convertDoubleToBigDecimal(200))
        .categoryIds(new ArrayList<>())
        .accountIds(new ArrayList<>())
        .build();
  }

  public static List<Long> convertCategoryIdsToList(Long... categoryIds) {
    return Arrays.asList(categoryIds);
  }

  public static List<Long> convertAccountIdsToList(Long... accountIds) {
    return Arrays.asList(accountIds);
  }
}
