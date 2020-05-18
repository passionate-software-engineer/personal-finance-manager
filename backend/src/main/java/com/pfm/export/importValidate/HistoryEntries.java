package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;
import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryInfo;

import java.util.List;

public class HistoryEntries {

    private static final String EMPTY = "";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {
        if (inputData.getHistoryEntries() == null) {
            validationsResult.add("HistoryEntries are missing");
            return validationsResult;
        }
        for (HistoryEntry historyEntry : inputData.getHistoryEntries()) {

            if (checkDataMissing(historyEntry.getId())) {
                validationsResult.add("Entry Id is missing");
            } else {

                if (checkDataMissing(historyEntry.getDate())) {
                    validationsResult.add(historyEntry.getId() + " historyEntry data is missing");
                }

                for (HistoryInfo entry : historyEntry.getEntries()) {
                    if (checkDataMissing(entry.getId())) {
                        validationsResult.add(entry.getName() + " Id is missing");
                    }

                    if (checkDataMissing(entry.getName())) {
                        validationsResult.add(entry.getName() + " name is missing");
                    }

                    if (checkDataMissing(entry.getNewValue())) {
                        validationsResult.add(entry.getName() + " newValue is missing");
                    }

                    if (checkDataMissing(entry.getOldValue())) {
                        validationsResult.add(entry.getName() + " oldValue is missing");
                    }
                }

                if (checkDataMissing(historyEntry.getObject())) {
                    validationsResult.add(historyEntry.getId() + " object is missing");
                }

                if (checkDataMissing(historyEntry.getType())) {
                    validationsResult.add(historyEntry.getId() + " type is missing");
                }

                if (checkDataMissing(historyEntry.getUserId())) {
                    validationsResult.add(historyEntry.getId() + " userId is missing");
                }
            }
        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }

}
