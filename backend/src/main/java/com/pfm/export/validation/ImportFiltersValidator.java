package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportFiltersValidator extends HelperValidator {

  private static final String DATA_NAME = "filters";
  private static final String NAME = " name;";

  List<String> validate(List<ExportResult.ExportFilter> inputData) {

    List<String> validationResult = new ArrayList<>();
    StringBuilder incorrectFields = new StringBuilder();

    for (int i = 0; i < inputData.size(); i++) {

      incorrectFields.setLength(0);

      if (isDataIncorrect(inputData.get(i).getName())) {
        incorrectFields.append(NAME);
      }

      if (incorrectFields.length() > 0) {
        validationResult.add(createResultMessage(DATA_NAME, i, incorrectFields.toString()));
      }
    }

    return validationResult;
  }
}
