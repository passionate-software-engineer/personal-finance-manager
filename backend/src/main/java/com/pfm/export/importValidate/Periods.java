package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class Periods {

    private static final String EMPTY = "";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {
        if (inputData.getPeriods() == null) {
            validationsResult.add("Periods are missing");
            return validationsResult;
        }
        for (ExportResult.ExportPeriod period : inputData.getPeriods()) {
            validateStartOrEndAccountState(period.getAccountStateAtTheBeginningOfPeriod(), validationsResult);
            validateStartOrEndAccountState(period.getAccountStateAtTheEndOfPeriod(), validationsResult);

            if (checkDataMissing(period.getEndDate())) {
                validationsResult.add(period.getClass().getName() + " endDate is missing");
            }

            if (checkDataMissing(period.getStartDate())) {
                validationsResult.add(period.getClass().getName() + " startDate is missing");
            }

            validateStartOrEndSumOfAllFunds(period.getSumOfAllFundsAtTheBeginningOfPeriod(), validationsResult);
            validateStartOrEndSumOfAllFunds(period.getSumOfAllFundsAtTheEndOfPeriod(), validationsResult);

            for (ExportResult.ExportTransaction transaction : period.getTransactions()) {

                for (ExportResult.ExportAccountPriceEntry priceEntry : transaction.getAccountPriceEntries()) {
                    if (checkDataMissing(priceEntry.getAccount())) {
                        validationsResult.add(period.getClass().getName() + " " + priceEntry.getClass().getName()
                                + " account is missing");
                    }

                    if (checkDataMissing(priceEntry.getPrice())) {
                        validationsResult.add(period.getClass().getName() + " " + priceEntry.getClass().getName()
                                + " price is missing");
                    }
                }
                if (checkDataMissing(transaction.getDate())) {
                    validationsResult.add(period.getClass().getName() + " transaction has missing date");
                } else {

                    if (checkDataMissing(transaction.getCategory())) {
                        validationsResult.add("Transaction from " + transaction.getDate() + " had missing category");
                    }

                    if (checkDataMissing(transaction.getDescription())) {
                        validationsResult.add("Transaction from " + transaction.getDate() + " had missing description");
                    }
                }
            }
        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }

    private void validateStartOrEndAccountState(List<ExportResult.ExportAccount> period, List<String> validationsResult) {
        for (ExportResult.ExportAccount account : period) {
            if (checkDataMissing(account.getName())) {
                validationsResult.add(period.getClass().getName() + " has missing name");
            } else {

                if (checkDataMissing(account.getAccountType())) {
                    validationsResult.add(period.getClass().getName() + " accountType is missing");
                }

                if (checkDataMissing(account.isArchived())) {
                    validationsResult.add(period.getClass().getName() + " archivedStatus is missing");
                }

                if (checkDataMissing(account.getBalance())) {
                    validationsResult.add(period.getClass().getName() + " balance is missing");
                }

                if (checkDataMissing(account.getCurrency())) {
                    validationsResult.add(period.getClass().getName() + " currency is missing");
                }

                if (checkDataMissing(account.getLastVerificationDate())) {
                    validationsResult.add(period.getClass().getName() + " lastVerificationDate is missing");
                }
            }
        }
    }

    private void validateStartOrEndSumOfAllFunds(ExportResult.ExportFundsSummary period, List<String> validationsResult) {

        if (checkDataMissing(period)) {
            validationsResult.add(period.getClass().getName() + " currencyToFundsMap is missing");
        }

        if (checkDataMissing(period.getSumOfAllFundsInBaseCurrency())) {
            validationsResult.add(period.getClass().getName() + " sumOfAllFundsInBaseCurrency is missing");
        }
    }
}
