package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class SumOfAllFunds {

    private static final String EMPTY = "";

    void validate(ExportResult inputData, List<String> validationsResult) {

        if (checkDataMissing(inputData.getSumOfAllFundsAtTheEndOfExport())) {
            validationsResult.add("Sum of all Funds At The end Of Export is missing");
        }

        if(checkDataMissing(inputData.getSumOfAllFundsAtTheBeginningOfExport())) {
            validationsResult.add("Sum of all Funds At The Beginning Of Export is missing");
        }
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
