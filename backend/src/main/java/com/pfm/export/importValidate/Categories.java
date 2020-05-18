package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class Categories {

    private static final String EMPTY = "";
    private static final String CATEGORY_NAME_MISSING = "Category name is missing";
    private static final String PARENT_CATEGORY_NAME_MISSING = " parent category name is missing";
    private static final String PRIORITY_MISSING = " priority is missing";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {

        if (inputData.getCategories() == null) {
            validationsResult.add("Categories are missing");
        }

        for (ExportResult.ExportCategory category : inputData.getCategories()) {

            if (checkDataMissing(category.getName())) {
                validationsResult.add(CATEGORY_NAME_MISSING);
            } else {

                if (checkDataMissing(category.getParentCategoryName())) {
                    validationsResult.add(category.getName() + PARENT_CATEGORY_NAME_MISSING);
                }

                if (checkDataMissing(category.getPriority())) {
                    validationsResult.add(category.getName() + PRIORITY_MISSING);
                }
            }

        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
