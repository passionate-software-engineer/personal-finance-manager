package com.pfm.transaction.import1;

import java.util.function.Predicate;

public interface ParsedTransactionPropertyFilter {
  static Predicate<String> notEmpty() {
    return stringProperty -> !stringProperty.isEmpty();
  }
}
