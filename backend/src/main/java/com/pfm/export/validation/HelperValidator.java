package com.pfm.export.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HelperValidator {

  private static final String MAIN_MESSAGE = "All incorrect or missing fields in ";

  boolean isDataIncorrect(Object data) {
    return data == null || !StringUtils.hasText(data.toString());
  }

  String createResultMessage(String exportDataName, int numberInRow, String incorrectFields) {
    return MAIN_MESSAGE + exportDataName + " number: " + numberInRow + incorrectFields;
  }
}
