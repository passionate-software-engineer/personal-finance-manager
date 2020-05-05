package com.pfm.export.converter;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransactionMonthDateFormatterTest {

  @Test
  public void shouldReturnTheSameDateAfterParsingToStringAndBackToDate() {
    // given
    LocalDate date = LocalDate.now();
    String toString = new TransactionMonthDateFormatter().toString(date);

    // when
    LocalDate identity = new TransactionMonthDateFormatter().toLocalDate(toString);

    // then
    Assertions.assertEquals(date.getMonth(), identity.getMonth());
    Assertions.assertEquals(date.getYear(), identity.getYear());
  }

}
