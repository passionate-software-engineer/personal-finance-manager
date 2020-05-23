package com.pfm.export.importValidation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;

public class ImportInitialAccountsStateValidator {

  private static final String EMPTY = "";

  private static final String ACCOUNT_NAME_MISSING = "Account name is missing";
  private static final String TYPE_MISSING = " account has missing type";
  private static final String ARCHIVE_STATUS_MISSING = " account has missing archive status";
  private static final String BALANCE_MISSING = " account has missing balance";
  private static final String CURRENCY_MISSING = " account has missing currency";
  private static final String LAST_VERIFICATION_DATE_MISSING = " account has missing last verification date";

  List<String> validate(List<ExportResult.ExportAccount> inputData) {

    List<String> validationResult = new ArrayList<>();

    if (inputData != null) {

      for (ExportResult.ExportAccount account : inputData) {

        if (checkDataMissing(account.getName())) {
          validationResult.add(ACCOUNT_NAME_MISSING);
        } else {

          if (checkDataMissing(account.getAccountType())) {
            validationResult.add(account.getName() + TYPE_MISSING);
          }

          if (checkDataMissing(account.isArchived())) {
            validationResult.add(account.getName() + ARCHIVE_STATUS_MISSING);
          }

          if (checkDataMissing(account.getBalance())) {
            validationResult.add(account.getName() + BALANCE_MISSING);
          }

          if (checkDataMissing(account.getCurrency())) {
            validationResult.add(account.getName() + CURRENCY_MISSING);
          }

          if (checkDataMissing(account.getLastVerificationDate())) {
            validationResult.add(account.getName() + LAST_VERIFICATION_DATE_MISSING);
          }
        }
      }
    }
    return validationResult;
  }

  private boolean checkDataMissing(Object data) {
    return data == null || EMPTY.equals(data);
  }
}
