package com.pfm.export.converter;

import java.time.LocalDate;

public interface DateFormatter {

  String toString(LocalDate date);

  LocalDate toLocalDate(String key);
}
