package com.pfm.helpers;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;

import com.pfm.filter.FilterRequest;
import java.time.LocalDate;

public class TestFilterProvider {

  private static final LocalDate DATE_OF_1ST_MARCH_2018 = LocalDate.of(2018, 3, 1);
  private static final LocalDate DATE_OF_31ST_MARCH_2018 = LocalDate.of(2018, 3, 31);

  private static final FilterRequest FILTER_FOOD_EXPENSES =
      FilterRequest.builder()
          .name("Food expenses")
          .dateFrom(DATE_OF_1ST_MARCH_2018)
          .dateTo(DATE_OF_31ST_MARCH_2018)
          .priceFrom(convertDoubleToBigDecimal(100))
          .priceTo(convertDoubleToBigDecimal(300))
          .description("Food")
          .build();

  public static FilterRequest getFilterFoodExpenses() {
    return FILTER_FOOD_EXPENSES;
  }

}
