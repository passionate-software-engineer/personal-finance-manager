package com.pfm.transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.pfm.transaction.DateHelper.DateRange;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("unused")
class DateHelperTest {

  @MethodSource("shouldReturnCorrectDateRangeParams")
  @ParameterizedTest
  void shouldReturnCorrectDateRange(List<LocalDate> dates, Optional<DateRange> dateRangeExpected) {
    // given

    // when
    final Optional<DateRange> actual = DateHelper.getDateRange(dates);

    // then
    assertThat(actual.get().getFromDate(), is(equalTo(dateRangeExpected.get().getFromDate())));
    assertThat(actual.get().getToDate(), is(equalTo(dateRangeExpected.get().getToDate())));
    assertFalse(actual.get().getFromDate().isAfter(actual.get().getToDate()));
  }

  private static Stream<Object> shouldReturnCorrectDateRangeParams() {

    final LocalDate date1 = LocalDate.of(2010, 7, 17);
    final LocalDate date2 = LocalDate.of(2020, 1, 1);
    final LocalDate date3 = LocalDate.of(2020, 3, 20);
    final LocalDate date4 = LocalDate.of(2020, 4, 11);
    final LocalDate date5 = LocalDate.of(2020, 4, 12);
    final LocalDate date6 = LocalDate.of(2020, 4, 19);

    return Stream.of(
        Arguments.of(List.of(date1), Optional.of(DateRange.builder().fromDate(date1).toDate(date1).build())),
        Arguments.of(List.of(date1, date1, date1), Optional.of(DateRange.builder().fromDate(date1).toDate(date1).build())),
        Arguments.of(List.of(date2, date2, date2), Optional.of(DateRange.builder().fromDate(date2).toDate(date2).build())),
        Arguments.of(List.of(date6, date1, date3, date4, date2, date5), Optional.of(DateRange.builder().fromDate(date1).toDate(date6).build())),
        Arguments.of(List.of(date2, date1, date5), Optional.of(DateRange.builder().fromDate(date1).toDate(date5).build())));
  }

  @Test
  void shouldReturnOptionalEmptyForEmptyDatesList() {
    // given
    List<LocalDate> dates = Collections.emptyList();
    Optional<DateRange> expected = Optional.empty();

    // when
    final Optional<DateRange> actual = DateHelper.getDateRange(dates);

    // then
    assertThat(actual, is(equalTo(expected)));
  }

}
