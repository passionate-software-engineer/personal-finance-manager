package com.pfm.transaction;

import java.time.LocalDate;

public enum RecurrencePeriod {
  NONE(null),
  EVERY_DAY(LocalDate.now().plusDays(1L)),
  EVERY_WEEK(LocalDate.now().plusWeeks(1L)),
  EVERY_MONTH(LocalDate.now().plusMonths(1L));

  private final LocalDate nextOccurrence;

  RecurrencePeriod(LocalDate nextOccurrence) {
    this.nextOccurrence = nextOccurrence;
  }

  public LocalDate getNextOccurrenceDate() {
    return nextOccurrence;
  }

}
