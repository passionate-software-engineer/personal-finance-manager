package com.pfm.transaction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class DateHelper {

  boolean isPastDate(LocalDate date) {
    return date.isBefore(LocalDate.now());
  }

  boolean isFutureDate(LocalDate date) {
    return date.isAfter(LocalDate.now());
  }

  public static Optional<DateRange> getDateRange(Collection<LocalDate> dates) {
    if (dates.isEmpty()) {
      return Optional.empty();
    }
    LocalDate fromDate = dates.stream()
        .findFirst()
        .get();

    LocalDate toDate = fromDate;

    for (LocalDate date : dates) {
      if (date.isBefore(fromDate)) {
        fromDate = date;
      }
      if (date.isAfter(toDate)) {
        toDate = date;
      }
    }

    DateRange dateRange = DateRange.builder()
        .fromDate(fromDate)
        .toDate(toDate)
        .build();

    return Optional.of(dateRange);
  }

  @Builder
  @Getter
  public static class DateRange {

    private LocalDate fromDate;
    private LocalDate toDate;
  }
}
