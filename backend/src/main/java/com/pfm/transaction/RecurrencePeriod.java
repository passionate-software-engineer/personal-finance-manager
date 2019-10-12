package com.pfm.transaction;

import java.time.LocalDate;

//@Embeddable

public enum RecurrencePeriod {
  EVERY_DAY(Constants.NOW.plusDays(1L)),
  EVERY_WEEK(Constants.NOW.plusWeeks(1L)),
  EVERY_MONTH(Constants.NOW.plusMonths(1L));

  private final LocalDate plusPeriod;

  RecurrencePeriod(LocalDate plusPeriod) {
    this.plusPeriod = plusPeriod;
  }

  public LocalDate getValue() {
    return plusPeriod;
  }

  private static class Constants {

    static final LocalDate NOW = LocalDate.now();
  }
}
