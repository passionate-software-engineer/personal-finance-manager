package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class SumOfAllFundsAtTheBeginningOfExport {

    private static final String EMPTY = "";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {
        if (checkDataMissing(inputData.getSumOfAllFundsAtTheBeginningOfExport())) {
            validationsResult.add("SumOfAllFundsAtTheBeginningOfExport is missing");
        }

        if (checkDataMissing(inputData.getSumOfAllFundsAtTheEndOfExport())) {
            validationsResult.add("SumOfAllFundsAtTheEndOfExport is missing");
        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
