package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import java.util.ArrayList;
import java.util.List;

public class ImportAccountsStateValidator {

  private static final String EMPTY = "";

  private static final String ACCOUNT_NAME_MISSING = "Account name is missing in ";
  private static final String TYPE_MISSING = " account has missing type in ";
  private static final String BALANCE_MISSING = " account has missing balance in ";
  private static final String CURRENCY_MISSING = " account has missing currency in ";
  private static final String LAST_VERIFICATION_DATE_MISSING = " account has missing last verification date in ";

  List<String> validate(List<ExportResult.ExportAccount> inputData, String currentPlaceName) {

    List<String> validationResult = new ArrayList<>();

    if (inputData != null) {

      for (ExportResult.ExportAccount account : inputData) {

        if (checkDataMissing(account.getName())) {
          validationResult.add(ACCOUNT_NAME_MISSING + currentPlaceName);
        } else {

          if (checkDataMissing(account.getAccountType())) {
            validationResult.add(account.getName() + TYPE_MISSING + currentPlaceName);
          }

          if (checkDataMissing(account.getBalance())) {
            validationResult.add(account.getName() + BALANCE_MISSING + currentPlaceName);
          }

          if (checkDataMissing(account.getCurrency())) {
            validationResult.add(account.getName() + CURRENCY_MISSING + currentPlaceName);
          }

          if (checkDataMissing(account.getLastVerificationDate())) {
            validationResult.add(account.getName() + LAST_VERIFICATION_DATE_MISSING + currentPlaceName);
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
