package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImportValidator {

    private static Categories categories;
    private static Filters filters;
    private static FinalAccountsState finalAccountsState;
    private static HistoryEntries historyEntries;
    private static InitialAccountsState initialAccountsState;
    private static Periods periods;
    private static SumOfAllFundsAtTheBeginningOfExport sumOfAllFundsAtTheBeginningOfExport;

    List<String> validate(ExportResult inputData) {
        List<String> validationsResult = new ArrayList<>();
        categories.validate(inputData, validationsResult);
        filters.validate(inputData, validationsResult);
        finalAccountsState.validate(inputData, validationsResult);
        historyEntries.validate(inputData, validationsResult);
        initialAccountsState.validate(inputData, validationsResult);
        periods.validate(inputData, validationsResult);
        sumOfAllFundsAtTheBeginningOfExport.validate(inputData, validationsResult);
        return validationsResult;
    }
}
