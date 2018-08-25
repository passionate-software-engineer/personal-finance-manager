package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.filter.FilterRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFilterProvider {

  private static final LocalDate DATE_OF_1ST_MARCH_2018 = LocalDate.of(2018, 3, 1);
  private static final LocalDate DATE_OF_31ST_MARCH_2018 = LocalDate.of(2018, 3, 31);
  private static final LocalDate DATE_OF_1ST_JANUARY_2018 = LocalDate.of(2018, 1, 1);
  private static final LocalDate DATE_OF_31ST_DECEMBER_2018 = LocalDate.of(2018, 12, 31);

  private static final FilterRequest FILTER_REQUEST_FOOD_EXPENSES =
      FilterRequest.builder()
          .name("Food expenses")
          .dateFrom(DATE_OF_1ST_MARCH_2018)
          .dateTo(DATE_OF_31ST_MARCH_2018)
          .priceFrom(convertDoubleToBigDecimal(100))
          .priceTo(convertDoubleToBigDecimal(300))
          .description("Food")
          .build();

  private static final FilterRequest FILTER_REQUEST_CAR_EXPENSES =
      FilterRequest.builder()
          .name("Car expenses")
          .dateFrom(DATE_OF_1ST_JANUARY_2018)
          .dateTo(DATE_OF_31ST_DECEMBER_2018)
          .build();

  private static final FilterRequest FILTER_REQUEST_HOME_EXPENSES_UP_TO_200 =
      FilterRequest.builder()
          .name("Home expenses up to 200$")
          .priceTo(convertDoubleToBigDecimal(200))
          .build();

  public static FilterRequest getFilterRequestFoodExpenses() {
    return copy(FILTER_REQUEST_FOOD_EXPENSES);
  }

  public static FilterRequest getFilterRequestCarExpenses() {
    return copy(FILTER_REQUEST_CAR_EXPENSES);
  }

  public static FilterRequest getFilterRequestHomeExpensesUpTo200() {
    return copy(FILTER_REQUEST_HOME_EXPENSES_UP_TO_200);
  }

  private static FilterRequest copy(FilterRequest filterRequestToCopy) {
    return FilterRequest.builder()
        .accountIds(filterRequestToCopy.getAccountIds() == null ? new ArrayList<>() : new ArrayList<>(filterRequestToCopy.getAccountIds()))
        .categoryIds(filterRequestToCopy.getCategoryIds() == null ? new ArrayList<>() : new ArrayList<>(filterRequestToCopy.getCategoryIds()))
        .dateTo(filterRequestToCopy.getDateTo())
        .dateFrom(filterRequestToCopy.getDateFrom())
        .priceFrom(filterRequestToCopy.getPriceFrom())
        .priceTo(filterRequestToCopy.getPriceTo())
        .description((filterRequestToCopy.getDescription()))
        .name(filterRequestToCopy.getName())
        .build();
  }

  public static List<Long> convertCategoryIdsToList(Long... categoryIds) {
    return Arrays.asList(categoryIds);
  }

  public static List<Long> convertAccountIdsToList(Long... accountIds) {
    return Arrays.asList(accountIds);
  }
}
