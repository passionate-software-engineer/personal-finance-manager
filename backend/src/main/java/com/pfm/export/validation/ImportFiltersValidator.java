package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportFiltersValidator extends HelperValidator {

  private static final String DATA_NAME = "filters";
  private static final String NAME = " name;";
  private static final String ACCOUNTS = " accounts;";
  private static final String CATEGORIES = " categories;";
  private static final String DATA_FROM = " date from;";
  private static final String DATA_TO = " date to;";
  private static final String DESCRIPTION = " description;";
  private static final String PRICE_FROM = " price from;";
  private static final String PRICE_TO = " price to;";

  List<String> validate(List<ExportResult.ExportFilter> inputData) {

    List<String> validationResult = new ArrayList<>();

    for (int i = 0; i < inputData.size(); i++) {

      StringBuilder incorrectFields = new StringBuilder();

      if (checkDataMissing(inputData.get(i).getName())) {
        incorrectFields.append(NAME);
      }
      if (checkDataMissing(inputData.get(i).getAccounts())) {
        incorrectFields.append(ACCOUNTS);
      }
      if (checkDataMissing(inputData.get(i).getCategories())) {
        incorrectFields.append(CATEGORIES);
      }
      if (checkDataMissing(inputData.get(i).getDateFrom())) {
        incorrectFields.append(DATA_FROM);
      }
      if (checkDataMissing(inputData.get(i).getDateTo())) {
        incorrectFields.append(DATA_TO);
      }
      if (checkDataMissing(inputData.get(i).getDescription())) {
        incorrectFields.append(DESCRIPTION);
      }
      if (checkDataMissing(inputData.get(i).getPriceFrom())) {
        incorrectFields.append(PRICE_FROM);
      }
      if (checkDataMissing(inputData.get(i).getPriceTo())) {
        incorrectFields.append(PRICE_TO);
      }

      if (incorrectFields.length() > 0) {
        validationResult.add(createResultMessage(DATA_NAME, i, incorrectFields.toString()));
      }
    }

    return validationResult;
  }
}
