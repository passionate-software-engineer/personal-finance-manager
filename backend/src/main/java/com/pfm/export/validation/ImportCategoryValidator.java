package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportCategoryValidator extends HelperValidator {

  private static final String DATA_NAME = "category";
  private static final String NAME = " name;";
  private static final String PRIORITY = " priority;";

  List<String> validate(List<ExportResult.ExportCategory> inputData) {

    List<String> validationResult = new ArrayList<>();
    StringBuilder incorrectFields = new StringBuilder();

    for (int i = 0; i < inputData.size(); i++) {

      incorrectFields.setLength(0);

      if (checkDataMissing(inputData.get(i).getName())) {
        incorrectFields.append(NAME);
      }
      if (checkPriorityFormat(inputData.get(i).getPriority())) {
        incorrectFields.append(PRIORITY);
      }

      if (incorrectFields.length() > 0) {
        validationResult.add(createResultMessage(DATA_NAME, i, incorrectFields.toString()));
      }
    }

    return validationResult;
  }

  private boolean checkPriorityFormat(int priority) {
    return !(priority >= 0 && priority <= 100);
  }
}
