package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportCategoryValidator extends HelperValidator {

  private static final String DATA_NAME = "category";
  private static final String NAME = " name;";
  private static final String PARENT_CATEGORY_NAME = " parent category name;";
  private static final String PRIORITY = " priority;";

  List<String> validate(List<ExportResult.ExportCategory> inputData) {

    List<String> validationResult = new ArrayList<>();

    for (int i = 0; i < inputData.size(); i++) {

      StringBuilder incorrectFields = new StringBuilder();

      if (checkDataMissing(inputData.get(i).getName())) {
        incorrectFields.append(NAME);
      }
      if (checkDataMissing(inputData.get(i).getParentCategoryName())) {
        incorrectFields.append(PARENT_CATEGORY_NAME);
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
