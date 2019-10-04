package com.pfm.transaction;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateHelper {

 boolean isPastDate(LocalDate date) {
    return date.isBefore(LocalDate.now());
  }

  boolean isFutureDate(LocalDate date) {
    return date.isAfter(LocalDate.now());
  }
}
