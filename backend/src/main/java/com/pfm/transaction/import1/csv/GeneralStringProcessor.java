package com.pfm.transaction.import1.csv;

public class GeneralStringProcessor {

  public String replaceThenTrim(String valueToProcess, String target, String replacement) {
    String stringValue = valueToProcess;
    if (stringValue.contains(target)) {
      stringValue = stringValue.replace(target, replacement);
    }
    return stringValue.trim();
  }
}
