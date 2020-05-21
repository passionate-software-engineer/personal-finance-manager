package com.pfm.export.validate;

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
  private static SumOfAllFunds sumOfAllFunds;

  public List<String> validate(ExportResult inputData) {

    List<String> validationsResult = new ArrayList<>();

    categories.validate(inputData.getCategories(), validationsResult);
    filters.validate(inputData.getFilters(), validationsResult);
    finalAccountsState.validate(inputData.getFinalAccountsState(), validationsResult);
    historyEntries.validate(inputData.getHistoryEntries(), validationsResult);
    initialAccountsState.validate(inputData.getInitialAccountsState(), validationsResult);
    periods.validate(inputData.getPeriods(), validationsResult);
    sumOfAllFunds.validate(inputData, validationsResult);

    return validationsResult;
  }
}
