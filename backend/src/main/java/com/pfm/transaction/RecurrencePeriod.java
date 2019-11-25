package com.pfm.transaction;

import java.time.LocalDate;

public enum RecurrencePeriod {
  NONE,
  EVERY_DAY,
  EVERY_WEEK,
  EVERY_MONTH;

  LocalDate getNextOccurrenceDate() {
    final LocalDate now = LocalDate.now();
    LocalDate date = null;
    switch (this) {
      case EVERY_DAY:
        date = now.plusDays(1L);
        break;
      case EVERY_WEEK:
        date = now.plusWeeks(1L);
        break;
      case EVERY_MONTH:
        date = now.plusMonths(1L);
        break;
      default:
    }
    return date;
  }
}



