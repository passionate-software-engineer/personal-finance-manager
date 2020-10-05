package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImportValidator {

  private static final String INITIAL_ACCOUNT_STATE = "initial ";
  private static final String FINAL_ACCOUNT_STATE = "final ";

  private final ImportCategoryValidator categories;
  private final ImportFiltersValidator filters;
  private final ImportHistoryEntriesValidator historyEntries;
  private final ImportAccountsStateValidator accountsState;
  private final ImportPeriodsValidator periods;

  public List<String> validate(ExportResult inputData) {

    List<String> validationsResult = new ArrayList<>();

    validationsResult.addAll(categories.validate(inputData.getCategories()));
    validationsResult.addAll(filters.validate(inputData.getFilters()));
    validationsResult.addAll(accountsState.validate(inputData.getInitialAccountsState(), INITIAL_ACCOUNT_STATE));
    validationsResult.addAll(accountsState.validate(inputData.getFinalAccountsState(), FINAL_ACCOUNT_STATE));
    validationsResult.addAll(historyEntries.validate(inputData.getHistoryEntries()));
    validationsResult.addAll(periods.validate(inputData.getPeriods()));

    return validationsResult;
  }
}
