package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImportValidator {

  private static ImportCategoryValidator categories;
  private static ImportFiltersValidator filters;
  private static ImportHistoryEntriesValidator historyEntries;
  private static ImportAccountsStateValidator accountsState;
  private static ImportPeriodsValidator periods;
  private static ImportSumOfAllFundsValidator sumOfAllFunds;

  public List<String> validate(ExportResult inputData) {

    List<String> validationsResult = new ArrayList<>();

    validationsResult.addAll(categories.validate(inputData.getCategories()));
    validationsResult.addAll(filters.validate(inputData.getFilters()));
    validationsResult.addAll(accountsState.validate(inputData.getInitialAccountsState(), "initial accounts state"));
    validationsResult.addAll(accountsState.validate(inputData.getFinalAccountsState(), "final accounts state"));
    validationsResult.addAll(historyEntries.validate(inputData.getHistoryEntries()));
    validationsResult.addAll(periods.validate(inputData.getPeriods()));
    validationsResult.addAll(sumOfAllFunds.validate(inputData));

    return validationsResult;
  }
}
