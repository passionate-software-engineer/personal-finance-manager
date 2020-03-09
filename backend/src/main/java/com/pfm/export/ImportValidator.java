package com.pfm.export;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImportValidator {

  public static final String EMPTY = "";

  List<String> validate(ExportResult inputData) {
    List<String> validationResults = new ArrayList<>();
    for (ExportResult.ExportFilter filter : inputData.getFilters()) {
      if (filter.getName() == null || EMPTY.equals(filter.getName().trim())) {
        validationResults.add("Filter is missing name");
      }
    }
    return validationResults;
  }
}
