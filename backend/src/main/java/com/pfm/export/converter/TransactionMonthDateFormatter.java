package com.pfm.export.converter;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class TransactionMonthDateFormatter implements DateFormatter {

  public static final String DATE_FORMAT = "%04d-%02d-01";

  @Override
  public String toString(LocalDate date) {
    return String.format(DATE_FORMAT, date.getYear(), date.getMonth().getValue());
  }

  @Override
  public LocalDate toLocalDate(String date) {
    return LocalDate.parse(date);
  }
}
