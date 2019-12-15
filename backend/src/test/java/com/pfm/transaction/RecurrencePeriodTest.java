package com.pfm.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RecurrencePeriodTest {

  @Test
  void shouldReturnNullForNotRecurrentEnumLiteral() {
    //given
    LocalDate expected = null;

    //when
    final LocalDate actual = RecurrencePeriod.NONE.getNextOccurrenceDate();

    //then
    assertEquals(actual, expected);
  }
}
