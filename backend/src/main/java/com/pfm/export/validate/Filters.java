package com.pfm.export.validate;

import com.pfm.export.ExportResult;
import java.util.List;

public class Filters {

  private static final String EMPTY = "";

  private static final String FILTER_NAME_MISSING = "Filter name is missing";
  private static final String ACCOUNTS_MISSING = " filter has missing accounts";
  private static final String CATEGORIES_MISSING = " filter has missing categories";
  private static final String DATA_FROM_MISSING = " filter has missing date from";
  private static final String DATA_TO_MISSING = " filter has missing date to";
  private static final String DESCRIPTION_MISSING = " filter has missing description";
  private static final String PRICE_FROM_MISSING = " filter has missing price from";
  private static final String PRICE_TO_MISSING = " filter has missing price to";

  void validate(List<ExportResult.ExportFilter> inputData, List<String> validationsResult) {

    if (inputData != null) {

      for (ExportResult.ExportFilter filter : inputData) {

        if (checkDataMissing(filter.getName())) {
          validationsResult.add(FILTER_NAME_MISSING);
        } else {

          if (checkDataMissing(filter.getAccounts())) {
            validationsResult.add(filter.getName() + ACCOUNTS_MISSING);
          }

          if (checkDataMissing(filter.getCategories())) {
            validationsResult.add(filter.getName() + CATEGORIES_MISSING);
          }

          if (checkDataMissing(filter.getDateFrom())) {
            validationsResult.add(filter.getName() + DATA_FROM_MISSING);
          }

          if (checkDataMissing(filter.getDateTo())) {
            validationsResult.add(filter.getName() + DATA_TO_MISSING);
          }

          if (checkDataMissing(filter.getDescription())) {
            validationsResult.add(filter.getName() + DESCRIPTION_MISSING);
          }

          if (checkDataMissing(filter.getPriceFrom())) {
            validationsResult.add(filter.getName() + PRICE_FROM_MISSING);
          }

          if (checkDataMissing(filter.getPriceTo())) {
            validationsResult.add(filter.getName() + PRICE_TO_MISSING);
          }
        }
      }
    }
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
