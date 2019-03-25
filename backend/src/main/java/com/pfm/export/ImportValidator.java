package com.pfm.export;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImportValidator {

  List<String> validate(long userId, ExportResult inputData) {
    List<String> validationResults = new ArrayList<>();
    for (ExportResult.ExportFilter filter : inputData.getFilters()) {
      if (filter.getName() == null || "".equals(filter.getName().trim())) {
        validationResults.add("Filter is missing name");
      }
    }
    return validationResults;
  }
}
