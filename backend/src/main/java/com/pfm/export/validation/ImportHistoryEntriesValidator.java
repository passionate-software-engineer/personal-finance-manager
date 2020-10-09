package com.pfm.export.validation;

import com.pfm.history.HistoryEntry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportHistoryEntriesValidator extends HelperValidator {

  private static final String DATA_NAME = "history entries";
  private static final String DATE = " date;";
  private static final String OBJECT = " object;";
  private static final String TYPE = " type;";
  private static final String ENTRIES = " entries;";
  private static final String CHILD_NAME = " name;";
  private static final String CHILD_NEW_VALUE = " new value;";

  List<String> validate(List<HistoryEntry> inputData) {

    List<String> validationResult = new ArrayList<>();
    StringBuilder incorrectFields = new StringBuilder();
    StringBuilder incorrectChildFields = new StringBuilder();

    if (inputData != null) {
      for (int i = 0; i < inputData.size(); i++) {

        incorrectFields.setLength(0);

        if (isDataIncorrect(inputData.get(i).getDate())) {
          incorrectFields.append(DATE);
        }
        if (isDataIncorrect(inputData.get(i).getObject())) {
          incorrectFields.append(OBJECT);
        }
        if (isDataIncorrect(inputData.get(i).getType())) {
          incorrectFields.append(TYPE);
        }
        if (inputData.get(i).getEntries() == null) {
          incorrectFields.append(ENTRIES);
        } else {
          for (int j = 0; j < inputData.get(i).getEntries().size(); j++) {

            incorrectChildFields.setLength(0);

            if (isDataIncorrect(inputData.get(i).getEntries().get(j).getName())) {
              incorrectChildFields.append(CHILD_NAME);
            }
            if (isDataIncorrect(inputData.get(i).getEntries().get(j).getNewValue())) {
              incorrectChildFields.append(CHILD_NEW_VALUE);
            }

            if (incorrectChildFields.length() > 0) {
              incorrectFields.append(getChildMessage(incorrectChildFields.toString(), j));
            }
          }
        }

        if (incorrectFields.length() > 0) {
          validationResult.add(createResultMessage(DATA_NAME, i, incorrectFields.toString()));
        }
      }
    }

    return validationResult;
  }

  private String getChildMessage(String incorrectFields, int numberInRow) {
    return " missing in entry number:" + numberInRow + incorrectFields;
  }
}
