package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportSumOfAllFundsValidator {

  private static final String EMPTY = "";

  private static final String SUM_AT_THE_BEGINNING_OF_EXPORT_MISSING = "Sum of all Funds At The Beginning Of Export is missing";
  private static final String SUM_AT_THE_END_OF_EXPORT_MISSING = "Sum of all Funds At The End Of Export is missing";

  List<String> validate(ExportResult inputData) {

    List<String> validationResult = new ArrayList<>();

    if (checkDataMissing(inputData.getSumOfAllFundsAtTheBeginningOfExport())) {
      validationResult.add(SUM_AT_THE_BEGINNING_OF_EXPORT_MISSING);
    }

    if (checkDataMissing(inputData.getSumOfAllFundsAtTheEndOfExport())) {
      validationResult.add(SUM_AT_THE_END_OF_EXPORT_MISSING);
    }

    return validationResult;
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
