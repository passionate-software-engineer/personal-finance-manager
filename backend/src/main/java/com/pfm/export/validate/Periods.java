package com.pfm.export.validate;

import com.pfm.export.ExportResult;

import java.util.List;

public class Periods {

  private static final String EMPTY = "";

  private static final String PERIOD_START_DATE_MISSING = "Period has missing start date";
  private static final String PERIOD_END_DATE_MISSING = "Period has missing end date";
  private static final String CHILD_ACCOUNT_NAME_MISSING = "Account has missing name for period form ";
  private static final String CHILD_ACCOUNT_TYPE_MISSING = " account has missing type for period from ";
  private static final String CHILD_ARCHIVE_STATUS_MISSING = " account has missing archive status for period from ";
  private static final String CHILD_BALANCE_MISSING = " account has missing balance for period from ";
  private static final String CHILD_CURRENCY_MISSING = " account has missing currency for period from ";
  private static final String CHILD_LAST_VERIFICATION_DATE_MISSING =
          " account has missing last verification date for period from ";
  private static final String TRANSACTION_DATE_MISSING = "Transaction has missing date for period from ";
  private static final String TRANSACTION_ACCOUNT_MISSING = " has missing account for period from ";
  private static final String TRANSACTION_PRICE_MISSING = " has missing price for period from ";
  private static final String TRANSACTION_CATEGORY_MISSING = " has missing category for period from ";
  private static final String TRANSACTION_DESCRIPTION_MISSING = " has missing description for period from ";
  private static final String CURRENCY_TO_FOUNDS_MAP_MISSING = "Currency founds missing for period from ";
  private static final String SUM_OF_ALL_FOUNDS_IN_BASE_CURRENCY_MISSING = "Sum of all founds " +
          "missing for period from ";

  void validate(List<ExportResult.ExportPeriod> inputData, List<String> validationsResult) {

    if (inputData != null) {

      for (ExportResult.ExportPeriod period : inputData) {

        if (checkDataMissing(period.getStartDate())) {
          validationsResult.add(PERIOD_START_DATE_MISSING);

        } else if (checkDataMissing(period.getEndDate())) {
          validationsResult.add(PERIOD_END_DATE_MISSING);
        } else {

          validateStartOrEndAccountState(period.getAccountStateAtTheBeginningOfPeriod(), validationsResult,
                  period);
          validateStartOrEndAccountState(period.getAccountStateAtTheEndOfPeriod(), validationsResult,
                  period);

          validateStartOrEndSumOfAllFunds(period.getSumOfAllFundsAtTheBeginningOfPeriod(), validationsResult,
                  period);
          validateStartOrEndSumOfAllFunds(period.getSumOfAllFundsAtTheEndOfPeriod(), validationsResult,
                  period);

          if (period.getTransactions() != null) {

            for (ExportResult.ExportTransaction transaction : period.getTransactions()) {

              if (checkDataMissing(transaction.getDate())) {
                validationsResult.add(TRANSACTION_DATE_MISSING
                        + period.getStartDate() + " to " + period.getEndDate());
              } else {

                if (transaction.getAccountPriceEntries() != null) {

                  for (ExportResult.ExportAccountPriceEntry priceEntry : transaction.getAccountPriceEntries()) {
                    if (checkDataMissing(priceEntry.getAccount())) {
                      validationsResult.add("Transaction at: " + transaction.getDate()
                              + TRANSACTION_ACCOUNT_MISSING
                              + period.getStartDate() + " to " + period.getEndDate());
                    }

                    if (checkDataMissing(priceEntry.getPrice())) {
                      validationsResult.add("Transaction at: " + transaction.getDate()
                              + TRANSACTION_PRICE_MISSING
                              + period.getStartDate() + " to " + period.getEndDate());
                    }
                  }
                }

                if (checkDataMissing(transaction.getCategory())) {
                  validationsResult.add("Transaction at " + transaction.getDate()
                          + TRANSACTION_CATEGORY_MISSING
                          + period.getStartDate() + " to " + period.getEndDate());
                }

                if (checkDataMissing(transaction.getDescription())) {
                  validationsResult.add("Transaction from " + transaction.getDate()
                          + TRANSACTION_DESCRIPTION_MISSING
                          + period.getStartDate() + " to " + period.getEndDate());
                }
              }
            }
          }
        }
      }
    }
  }

  private void validateStartOrEndAccountState(List<ExportResult.ExportAccount> period, List<String> validationsResult,
                                              ExportResult.ExportPeriod currentPeriod) {

    if (period != null) {

      for (ExportResult.ExportAccount account : period) {

        if (checkDataMissing(account.getName())) {
          validationsResult.add(CHILD_ACCOUNT_NAME_MISSING
                  + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
        } else {

          if (checkDataMissing(account.getAccountType())) {
            validationsResult.add(account.getName() + CHILD_ACCOUNT_TYPE_MISSING
                    + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
          }

          if (checkDataMissing(account.isArchived())) {
            validationsResult.add(account.getName() + CHILD_ARCHIVE_STATUS_MISSING
                    + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
          }

          if (checkDataMissing(account.getBalance())) {
            validationsResult.add(account.getName() + CHILD_BALANCE_MISSING
                    + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
          }

          if (checkDataMissing(account.getCurrency())) {
            validationsResult.add(account.getName() + CHILD_CURRENCY_MISSING
                    + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
          }

          if (checkDataMissing(account.getLastVerificationDate())) {
            validationsResult.add(account.getName() + CHILD_LAST_VERIFICATION_DATE_MISSING
                    + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
          }
        }
      }
    }
  }

  private void validateStartOrEndSumOfAllFunds(ExportResult.ExportFundsSummary period, List<String> validationsResult,
                                               ExportResult.ExportPeriod currentPeriod) {

    if (period.getCurrencyToFundsMap() != null) {
      validationsResult.add(CURRENCY_TO_FOUNDS_MAP_MISSING
              + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
    }

    if (checkDataMissing(period.getSumOfAllFundsInBaseCurrency())) {
      validationsResult.add(SUM_OF_ALL_FOUNDS_IN_BASE_CURRENCY_MISSING
              + currentPeriod.getStartDate() + " to " + currentPeriod.getEndDate());
    }
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
