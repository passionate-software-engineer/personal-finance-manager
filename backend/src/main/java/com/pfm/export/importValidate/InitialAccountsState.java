package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class InitialAccountsState {

    private static final String EMPTY = "";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {
        if (inputData.getInitialAccountsState() == null) {
            validationsResult.add("Filters are missing");
            return validationsResult;
        }

        for (ExportResult.ExportAccount account : inputData.getInitialAccountsState()) {
            if (checkDataMissing(account.getName())) {
                validationsResult.add("Account name is missing");
            } else {

                if (checkDataMissing(account.getAccountType())) {
                    validationsResult.add(account.getName() + " accountType are missing");
                }

                if (checkDataMissing(account.isArchived())) {
                    validationsResult.add(account.getName() + " archivedStatus are missing");
                }

                if (checkDataMissing(account.getBalance())) {
                    validationsResult.add(account.getName() + " balance is missing");
                }

                if (checkDataMissing(account.getCurrency())) {
                    validationsResult.add(account.getName() + " currency is missing");
                }

                if (checkDataMissing(account.getLastVerificationDate())) {
                    validationsResult.add(account.getName() + " lastVerificationDate is missing");
                }
            }
        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
