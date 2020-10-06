package com.pfm.export.validation;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImportPeriodsValidator extends HelperValidator {

  private static final String DATA_NAME = "periods";
  private static final String BEGINNING_ACCOUNT_STATE = " beginning account number: ";
  private static final String END_ACCOUNT_STATE = " end account number: ";
  private static final String START_DATE = " start date;";
  private static final String END_DATE = " end date;";
  private static final String BEGINNING_SUM_OF_ALL_FOUNDS = " beginning sum of all founds;";
  private static final String BEGINNING_SUM_OF_ALL_FOUNDS_IN_CURRENCY = " beginning sum of all founds in currency;";
  private static final String END_SUM_OF_ALL_FOUNDS = " end sum of all founds;";
  private static final String END_SUM_OF_ALL_FOUNDS_IN_CURRENCY = " end sum of all founds in currency;";
  private static final String TRANSACTIONS = " transactions;";
  private static final String MAIN_TRANSACTION_MESSAGE = " in transaction number: ";
  private static final String TRANSACTION_DATE = " date;";
  private static final String TRANSACTION_CATEGORY = " category;";
  private static final String TRANSACTION_DESCRIPTION = " description;";
  private static final String TRANSACTION_ENTRIES = " entries;";
  private static final String TRANSACTION_ENTRIES_MAIN_MESSAGE = " in entry number: ";
  private static final String TRANSACTION_ENTRY_ACCOUNT = " account;";
  private static final String TRANSACTION_ENTRY_PRICE = " price;";

  private final ImportAccountsStateValidator importAccountsStateValidator;

  List<String> validate(List<ExportResult.ExportPeriod> inputData) {

    List<String> validationResult = new ArrayList<>();
    StringBuilder incorrectFields = new StringBuilder();

    if (inputData != null) {
      for (int i = 0; i < inputData.size(); i++) {

        incorrectFields.setLength(0);

        if (checkDataMissing(inputData.get(i).getStartDate())) {
          incorrectFields.append(START_DATE);
        }
        if (checkDataMissing(inputData.get(i).getEndDate())) {
          incorrectFields.append(END_DATE);
        }
        if (checkDataMissing(inputData.get(i).getSumOfAllFundsAtTheBeginningOfPeriod())) {
          incorrectFields.append(BEGINNING_SUM_OF_ALL_FOUNDS);
        } else {
          if (checkDataMissing(inputData.get(i).getSumOfAllFundsAtTheBeginningOfPeriod().getSumOfAllFundsInBaseCurrency())) {
            incorrectFields.append(BEGINNING_SUM_OF_ALL_FOUNDS_IN_CURRENCY);
          }
        }
        if (checkDataMissing(inputData.get(i).getSumOfAllFundsAtTheEndOfPeriod())) {
          incorrectFields.append(END_SUM_OF_ALL_FOUNDS);
        } else {
          if (checkDataMissing(inputData.get(i).getSumOfAllFundsAtTheEndOfPeriod().getSumOfAllFundsInBaseCurrency())) {
            incorrectFields.append(END_SUM_OF_ALL_FOUNDS_IN_CURRENCY);
          }
        }
        for (int j = 0; j < inputData.get(i).getAccountStateAtTheBeginningOfPeriod().size(); j++) {
          Optional<String> result = importAccountsStateValidator
              .validateAccount(inputData.get(i).getAccountStateAtTheBeginningOfPeriod().get(j));
          if (result.isPresent()) {
            incorrectFields.append(BEGINNING_ACCOUNT_STATE).append(j).append(result.get());
          }
        }
        for (int j = 0; j < inputData.get(i).getAccountStateAtTheEndOfPeriod().size(); j++) {
          Optional<String> result = importAccountsStateValidator
              .validateAccount(inputData.get(i).getAccountStateAtTheEndOfPeriod().get(j));
          if (result.isPresent()) {
            incorrectFields.append(END_ACCOUNT_STATE).append(j).append(result.get());
          }
        }

        if (inputData.get(i).getTransactions().size() == 0) {
          incorrectFields.append(TRANSACTIONS);
        } else {
          Optional<String> result = validateTransactions(List.copyOf(inputData.get(i).getTransactions()));
          result.ifPresent(incorrectFields::append);

          if (incorrectFields.length() > 0) {
            validationResult.add(createResultMessage(DATA_NAME, i, incorrectFields.toString()));
          }
        }
      }
    }

    return validationResult;
  }

  private Optional<String> validateTransactions(List<ExportTransaction> transactions) {

    StringBuilder incorrectTransactionsFields = new StringBuilder();

    for (int j = 0; j < transactions.size(); j++) {

      incorrectTransactionsFields.setLength(0);

      if (checkDataMissing(transactions.get(j).getDate())) {
        incorrectTransactionsFields.append(TRANSACTION_DATE);
      }
      if (checkDataMissing(transactions.get(j).getCategory())) {
        incorrectTransactionsFields.append(TRANSACTION_CATEGORY);
      }
      if (checkDataMissing(transactions.get(j).getDescription())) {
        incorrectTransactionsFields.append(TRANSACTION_DESCRIPTION);
      }

      if (checkDataMissing(transactions.get(j).getAccountPriceEntries())) {
        incorrectTransactionsFields.append(TRANSACTION_ENTRIES);
      } else {
        Optional<String> result = validateEntries(transactions.get(j).getAccountPriceEntries());
        result.ifPresent(incorrectTransactionsFields::append);
      }

      if (incorrectTransactionsFields.length() > 0) {
        return Optional.of(MAIN_TRANSACTION_MESSAGE + j + incorrectTransactionsFields.toString());
      }
    }
    return Optional.empty();
  }

  private Optional<String> validateEntries(List<ExportAccountPriceEntry> entries) {

    StringBuilder incorrectFieldsInTransactionAccounts = new StringBuilder();

    for (int k = 0; k < entries.size(); k++) {

      incorrectFieldsInTransactionAccounts.setLength(0);

      if (checkDataMissing(entries.get(k).getAccount())) {
        incorrectFieldsInTransactionAccounts.append(TRANSACTION_ENTRY_ACCOUNT);
      }
      if (checkDataMissing(entries.get(k).getPrice())) {
        incorrectFieldsInTransactionAccounts.append(TRANSACTION_ENTRY_PRICE);
      }

      if (incorrectFieldsInTransactionAccounts.length() > 0) {
        return Optional.of(TRANSACTION_ENTRIES_MAIN_MESSAGE + k + incorrectFieldsInTransactionAccounts.toString());
      }
    }
    return Optional.empty();
  }
}
