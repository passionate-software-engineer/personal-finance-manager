package com.pfm.transaction;

import java.time.LocalDate;

public enum RecurrencePeriod {
  NONE,
  EVERY_DAY,
  EVERY_WEEK,
  EVERY_MONTH;

  LocalDate getNextOccurrenceDate() {
    final LocalDate now = LocalDate.now();
    switch (this) {
      case EVERY_DAY:
        return now.plusDays(1L);
      case EVERY_WEEK:
        return now.plusWeeks(1L);
      case EVERY_MONTH:
        return now.plusMonths(1L);
      default:
    }
    return null;
  }
}
