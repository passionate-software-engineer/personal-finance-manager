package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class FinalAccountsState {

    private static final String EMPTY = "";

    private static final String ACCOUNT_NAME_MISSING = "Account name is missing";
    private static final String TYPE_MISSING = " account has missing type";
    private static final String ACCOUNT_ARCHIVE_STATUS_MISSING = " account has missing archive status";
    private static final String BALANCE_MISSING = " account has missing balance";
    private static final String CURRENCY_MISSING = " account has missing currency";
    private static final String LAST_VERIFICATION_DATE_MISSING = " account has missing last verification date";

    void validate(List<ExportResult.ExportAccount> inputData, List<String> validationsResult) {
        if (inputData != null) {

            for (ExportResult.ExportAccount account : inputData) {

                if (checkDataMissing(account.getName())) {
                    validationsResult.add(ACCOUNT_NAME_MISSING);
                } else {

                    if (checkDataMissing(account.getAccountType())) {
                        validationsResult.add(account.getName() + TYPE_MISSING);
                    }

                    if (checkDataMissing(account.isArchived())) {
                        validationsResult.add(account.getName() + ACCOUNT_ARCHIVE_STATUS_MISSING);
                    }

                    if (checkDataMissing(account.getBalance())) {
                        validationsResult.add(account.getName() + BALANCE_MISSING);
                    }

                    if (checkDataMissing(account.getCurrency())) {
                        validationsResult.add(account.getName() + CURRENCY_MISSING);
                    }

                    if (checkDataMissing(account.getLastVerificationDate())) {
                        validationsResult.add(account.getName() + LAST_VERIFICATION_DATE_MISSING);
                    }
                }
            }
        }
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
