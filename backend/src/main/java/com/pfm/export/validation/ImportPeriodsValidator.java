package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;

public class ImportPeriodsValidator {

  private ImportAccountsStateValidator importAccountsStateValidator = new ImportAccountsStateValidator();

  private static final String EMPTY = "";

  private static final String PERIOD_START_OR_END_DATE_MISSING = "Period has missing start or end date";
  private static final String TRANSACTION_DATE_MISSING = "Transaction has missing date for period from ";
  private static final String TRANSACTION_ACCOUNT_MISSING = " has missing account for period from ";
  private static final String TRANSACTION_PRICE_MISSING = " has missing price for period from ";
  private static final String TRANSACTION_CATEGORY_MISSING = " has missing category for period from ";
  private static final String TRANSACTION_DESCRIPTION_MISSING = " has missing description for period from ";
  private static final String CURRENCY_TO_FOUNDS_MAP_MISSING = "Currency founds missing in ";
  private static final String SUM_OF_ALL_FOUNDS_IN_BASE_CURRENCY_MISSING = "Sum of all founds missing in ";

  List<String> validate(List<ExportResult.ExportPeriod> inputData) {

    List<String> validationResult = new ArrayList<>();

    if (inputData != null) {

      for (ExportResult.ExportPeriod period : inputData) {

        if (checkDataMissing(period.getStartDate()) || checkDataMissing(period.getEndDate())) {
          validationResult.add(PERIOD_START_OR_END_DATE_MISSING);
        } else {

          List<String> beginningAccountsStateLogs = importAccountsStateValidator.validate(period.getAccountStateAtTheBeginningOfPeriod(),
              "beginning of period from " + period.getStartDate() + " to " + period.getEndDate());

          List<String> endAccountsStateLogs = importAccountsStateValidator.validate(period.getAccountStateAtTheEndOfPeriod(),
              "end of period from " + period.getStartDate() + " to " + period.getEndDate());

          if (!beginningAccountsStateLogs.isEmpty()) {
            validationResult.addAll(beginningAccountsStateLogs);
          }

          if (!endAccountsStateLogs.isEmpty()) {
            validationResult.addAll(endAccountsStateLogs);
          }

          if (period.getTransactions() != null) {

            for (ExportResult.ExportTransaction transaction : period.getTransactions()) {

              if (checkDataMissing(transaction.getDate())) {
                validationResult.add(TRANSACTION_DATE_MISSING
                    + period.getStartDate() + " to " + period.getEndDate());
              } else {

                if (transaction.getAccountPriceEntries() != null) {

                  for (ExportResult.ExportAccountPriceEntry priceEntry : transaction.getAccountPriceEntries()) {
                    if (checkDataMissing(priceEntry.getAccount())) {
                      validationResult.add("Transaction at: " + transaction.getDate()
                          + TRANSACTION_ACCOUNT_MISSING
                          + period.getStartDate() + " to " + period.getEndDate());
                    }

                    if (checkDataMissing(priceEntry.getPrice())) {
                      validationResult.add("Transaction at: " + transaction.getDate()
                          + TRANSACTION_PRICE_MISSING
                          + period.getStartDate() + " to " + period.getEndDate());
                    }
                  }
                }

                if (checkDataMissing(transaction.getCategory())) {
                  validationResult.add("Transaction at: " + transaction.getDate()
                      + TRANSACTION_CATEGORY_MISSING
                      + period.getStartDate() + " to " + period.getEndDate());
                }

                if (checkDataMissing(transaction.getDescription())) {
                  validationResult.add("Transaction at: " + transaction.getDate()
                      + TRANSACTION_DESCRIPTION_MISSING
                      + period.getStartDate() + " to " + period.getEndDate());
                }
              }
            }
          }

          if (period.getSumOfAllFundsAtTheBeginningOfPeriod() != null) {
            validateSumOfAllFunds(period.getSumOfAllFundsAtTheBeginningOfPeriod(), validationResult, period,
                "the beginning of period from ");
          }

          if (period.getSumOfAllFundsAtTheEndOfPeriod() != null) {
            validateSumOfAllFunds(period.getSumOfAllFundsAtTheEndOfPeriod(), validationResult, period,
                "the end of period from ");
          }
        }
      }
    }
    return validationResult;
  }

  private void validateSumOfAllFunds(ExportResult.ExportFundsSummary period, List<String> validationsResult,
      ExportResult.ExportPeriod currentPeriod, String currentPlace) {

    if (checkDataMissing(period.getCurrencyToFundsMap())) {
      validationsResult.add(CURRENCY_TO_FOUNDS_MAP_MISSING + currentPlace
          + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
    }

    if (checkDataMissing(period.getSumOfAllFundsInBaseCurrency())) {
      validationsResult.add(SUM_OF_ALL_FOUNDS_IN_BASE_CURRENCY_MISSING + currentPlace
          + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
    }
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
