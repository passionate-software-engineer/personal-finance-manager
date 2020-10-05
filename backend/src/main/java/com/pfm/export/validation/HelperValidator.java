package com.pfm.export.validation;

import org.springframework.stereotype.Component;

@Component
public abstract class HelperValidator {

  private static final String EMPTY = "";
  private static final String MAIN_MESSAGE = "All incorrect or missing fields in ";

  boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }

  String createResultMessage(String exportDataName, int numberInRow, String incorrectFields) {
    return MAIN_MESSAGE + exportDataName + " number: " + numberInRow + incorrectFields;
  }
}
