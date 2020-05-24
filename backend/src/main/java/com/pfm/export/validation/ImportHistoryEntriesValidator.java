package com.pfm.export.validation;

import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryInfo;
import java.util.ArrayList;
import java.util.List;

public class ImportHistoryEntriesValidator {

  private static final String EMPTY = "";

  private static final String PARENT_ID_MISSING = "History entry ID is missing";
  private static final String PARENT_DATE_MISSING = " history entry ID has missing date";
  private static final String CHILD_NAME_MISSING = "Entry ID has missing name in history entry ID:";
  private static final String CHILD_ID_MISSING = " entry has missing ID in history entry ID:";
  private static final String CHILD_NEW_VALUE_MISSING = " entry has missing new value in history entry ID:";
  private static final String CHILD_OLD_VALUE_MISSING = " entry has missing old value in history entry ID:";
  private static final String PARENT_OBJECT_MISSING = " history entry ID has missing object";
  private static final String PARENT_TYPE_MISSING = " history entry ID has missing type";

  List<String> validate(List<HistoryEntry> inputData) {

    List<String> validationResult = new ArrayList<>();

    if (inputData != null) {

      for (HistoryEntry historyEntry : inputData) {

        if (checkDataMissing(historyEntry.getId())) {
          validationResult.add(PARENT_ID_MISSING);
        } else {

          if (checkDataMissing(historyEntry.getDate())) {
            validationResult.add(PARENT_DATE_MISSING + historyEntry.getId());
          }

          if (historyEntry.getEntries() != null) {

            for (HistoryInfo entry : historyEntry.getEntries()) {
              if (checkDataMissing(entry.getName())) {
                validationResult.add(CHILD_NAME_MISSING + historyEntry.getId());
              } else {

                if (checkDataMissing(entry.getId())) {
                  validationResult.add(entry.getName() + CHILD_ID_MISSING + historyEntry.getId());
                }

                if (checkDataMissing(entry.getNewValue())) {
                  validationResult.add(entry.getName() + CHILD_NEW_VALUE_MISSING + historyEntry.getId());
                }

                if (checkDataMissing(entry.getOldValue())) {
                  validationResult.add(entry.getName() + CHILD_OLD_VALUE_MISSING + historyEntry.getId());
                }
              }
            }
          }

          if (checkDataMissing(historyEntry.getObject())) {
            validationResult.add(historyEntry.getId() + PARENT_OBJECT_MISSING);
          }

          if (checkDataMissing(historyEntry.getType())) {
            validationResult.add(historyEntry.getId() + PARENT_TYPE_MISSING);
          }
        }
      }
    }
    return validationResult;
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
