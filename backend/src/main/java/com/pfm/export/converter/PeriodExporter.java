package com.pfm.export.converter;

import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PeriodExporter {

  private final FundsExporter fundsExporter;
  private final AccountExporter accountExporter;
  private final TransactionExporter transactionExporter;
  private final DateFormatter dateFormatter;

  public List<ExportPeriod> export(long userId) {
    Map<String, List<ExportTransaction>> transactionsByMonth = transactionExporter.getTransactionsByMonth(userId);
    List<ExportAccount> accountsState = accountExporter.export(userId);

    List<ExportPeriod> periods = new ArrayList<>();

    for (Entry<String, List<ExportTransaction>> transactionsInMonth : transactionsByMonth.entrySet()) {

      List<ExportAccount> accountsStateAtThePeriodStart = subtractTransactionsFromAccounts(transactionsInMonth, accountsState);
      periods.add(calculatePeriod(accountsState, transactionsInMonth, accountsStateAtThePeriodStart, userId));

      accountsState = accountsStateAtThePeriodStart;
    }

    return periods;
  }

  private ExportPeriod calculatePeriod(List<ExportAccount> accountsState,
      Entry<String, List<ExportTransaction>> transactionsInMonth,
      List<ExportAccount> accountsStateAtThePeriodStart,
      long userId) {

    ExportFundsSummary sumOfAllFundsAtTheBeginningOfPeriod = fundsExporter.export(accountsStateAtThePeriodStart, userId);
    ExportFundsSummary sumOfAllFundsAtTheEndOfPeriod = fundsExporter.export(accountsState, userId);

    return ExportPeriod.builder()
        .accountStateAtTheBeginningOfPeriod(accountsStateAtThePeriodStart)
        .accountStateAtTheEndOfPeriod(accountsState)
        .sumOfAllFundsAtTheBeginningOfPeriod(sumOfAllFundsAtTheBeginningOfPeriod)
        .sumOfAllFundsAtTheEndOfPeriod(sumOfAllFundsAtTheEndOfPeriod)
        .startDate(dateFormatter.toLocalDate(transactionsInMonth.getKey()))
        .endDate(dateFormatter.toLocalDate(transactionsInMonth.getKey()).plusMonths(1).minusDays(1))
        .transactions(transactionsInMonth.getValue())
        .build();
  }

  private List<ExportAccount> subtractTransactionsFromAccounts(
      final Entry<String, List<ExportTransaction>> transactionsInMonth,
      final List<ExportAccount> exportAccounts) {

    Map<String, ExportAccount> accountNameToAccountMap = exportAccounts.stream()
        .collect(Collectors.toMap(ExportAccount::getName, Function.identity()));

    transactionsInMonth.getValue()
        .forEach(transaction -> transaction
            .getAccountPriceEntries()
            .forEach(exportAccountPriceEntry -> subtractPriceFromAccount(accountNameToAccountMap, exportAccountPriceEntry)));

    return new ArrayList<>(accountNameToAccountMap.values());
  }

  private void subtractPriceFromAccount(
      final Map<String, ExportAccount> accountsMap,
      final ExportAccountPriceEntry priceEntry) {

    ExportAccount account = accountsMap.get(priceEntry.getAccount());

    ExportAccount accountWithNewBalance = account.toBuilder()
        .balance(account.getBalance().subtract(priceEntry.getPrice()))
        .build();

    accountsMap.put(priceEntry.getAccount(), accountWithNewBalance);
  }

}
