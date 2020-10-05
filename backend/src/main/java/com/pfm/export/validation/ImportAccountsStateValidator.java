package com.pfm.export.validation;

import com.pfm.export.ExportResult.ExportAccount;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ImportAccountsStateValidator extends HelperValidator {

  private static final String NAME = " name;";
  private static final String ACCOUNT_TYPE = " account type;";
  private static final String BALANCE = " balance;";
  private static final String CURRENCY = " currency;";
  private static final String LAST_VERIFICATION_DATE = " last verification date;";

  List<String> validate(List<ExportAccount> inputData, String accountPlace) {

    List<String> validationResult = new ArrayList<>();

    for (int i = 0; i < inputData.size(); i++) {
      Optional<String> result = validateAccount(inputData.get(i));
      if (result.isPresent()) {
        validationResult.add(createResultMessage(accountPlace, i, result.get()));
      }
    }
    return validationResult;
  }

  Optional<String> validateAccount(ExportAccount inputData) {

    StringBuilder incorrectFields = new StringBuilder();

    if (checkDataMissing(inputData.getName())) {
      incorrectFields.append(NAME);
    }
    if (checkDataMissing(inputData.getAccountType())) {
      incorrectFields.append(ACCOUNT_TYPE);
    }
    if (checkDataMissing(inputData.getBalance())) {
      incorrectFields.append(BALANCE);
    }
    if (checkDataMissing(inputData.getCurrency())) {
      incorrectFields.append(CURRENCY);
    }
    if (checkDataMissing(inputData.getLastVerificationDate())) {
      incorrectFields.append(LAST_VERIFICATION_DATE);
    }

    if (incorrectFields.length() > 0) {
      return Optional.of(incorrectFields.toString());
    }

    return Optional.empty();
  }
}
