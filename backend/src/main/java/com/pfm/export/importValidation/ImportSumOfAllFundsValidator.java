package com.pfm.export.importValidation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;

public class ImportSumOfAllFundsValidator {

  private static final String EMPTY = "";

  List<String> validate(ExportResult inputData) {

    List<String> validationResult = new ArrayList<>();

    if (checkDataMissing(inputData.getSumOfAllFundsAtTheEndOfExport())) {
      validationResult.add("Sum of all Funds At The end Of Export is missing");
    }

    if (checkDataMissing(inputData.getSumOfAllFundsAtTheBeginningOfExport())) {
      validationResult.add("Sum of all Funds At The Beginning Of Export is missing");
    }

    return validationResult;
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
