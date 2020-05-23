package com.pfm.export.importValidation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;

public class ImportCategoryValidator {

  private static final String EMPTY = "";

  private static final String CATEGORY_NAME_MISSING = "Category name is missing";
  private static final String PARENT_CATEGORY_NAME_MISSING = " category has missing parent category name";
  private static final String PRIORITY_MISSING = " category has missing priority";

  List<String> validate(List<ExportResult.ExportCategory> inputData) {

    List<String> validationResult = new ArrayList<>();

    if (inputData != null) {

      for (ExportResult.ExportCategory category : inputData) {

        if (checkDataMissing(category.getName())) {
          validationResult.add(CATEGORY_NAME_MISSING);
        } else {

          if (checkDataMissing(category.getParentCategoryName())) {
            validationResult.add(category.getName() + PARENT_CATEGORY_NAME_MISSING);
          }

          if (checkPriorityFormat(category.getPriority())) {
            validationResult.add(category.getName() + PRIORITY_MISSING);
          }
        }
      }
    }
    return validationResult;
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }

  private boolean checkPriorityFormat(int priority) {
    return !(priority >= 0 && priority <= 100);
  }
}
