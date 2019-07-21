package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.filter.FilterService;
import com.pfm.history.HistoryEntryService;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportService {

  private TransactionService transactionService;
  private AccountService accountService;
  private CategoryService categoryService;
  private CurrencyService currencyService;
  private FilterService filterService;
  private HistoryEntryService historyEntryService;

  ExportResult exportData(long userId) {
    ExportResult result = new ExportResult();

    result.setFilters(prepareExportFilters(userId));
    result.setCategories(prepareExportCategories(userId));
    List<ExportAccount> accounts = convertToExportAccounts(accountService.getAccounts(userId));
    result.setFinalAccountsState(accounts);
    result.setSumOfAllFundsAtTheEndOfExport(calculateSumOfFunds(result.getFinalAccountsState(), userId));
    List<ExportTransaction> exportTransactions = convertTransactionsToExportTransactions(transactionService.getTransactions(userId), userId);
    Map<String, List<ExportTransaction>> monthToTransactionMap = groupTransactionsByMonth(exportTransactions);

    List<ExportPeriod> periods = generateExportPeriods(monthToTransactionMap, userId);
    result.setPeriods(periods);
    if (periods.size() > 0) {
      result.setInitialAccountsState(periods.get(periods.size() - 1).getAccountStateAtTheBeginningOfPeriod());
    }
    if (result.getInitialAccountsState().isEmpty()) {
      result.setInitialAccountsState(accounts);
    }

    result.setSumOfAllFundsAtTheBeginningOfExport(calculateSumOfFunds(result.getInitialAccountsState(), userId));
    result.setHistoryEntries(historyEntryService.prepareExportHistory(historyEntryService.getHistoryEntries(userId)));
    // TODO export / import filters
    return result;
  }

  private List<ExportPeriod> generateExportPeriods(Map<String, List<ExportTransaction>> monthToTransactionMap, long userId) {
    List<ExportAccount> accountsStateAtTheEndOfPeriod = convertToExportAccounts(accountService.getAccounts(userId));

    List<ExportPeriod> periods = new ArrayList<>();
    // Algorithm is starting from the current account state - we know what are the values in the accounts at the time of doing export
    // then every month (starting from most current) we subract values of transactions to get account states at the begining of period
    for (Entry<String, List<ExportTransaction>> transactionsInMonth : monthToTransactionMap.entrySet()) {

      List<ExportAccount> accountsStateAtTheBeginingOfPeriod = copyAccounts(accountsStateAtTheEndOfPeriod);
      subtractTransactionsValuesFromAccountStateToCalculateStateBeforeTransactions(transactionsInMonth, accountsStateAtTheBeginingOfPeriod);

      ExportPeriod period = ExportPeriod.builder()
          .accountStateAtTheBeginningOfPeriod(accountsStateAtTheBeginingOfPeriod)
          .accountStateAtTheEndOfPeriod(accountsStateAtTheEndOfPeriod)
          .sumOfAllFundsAtTheBeginningOfPeriod(calculateSumOfFunds(accountsStateAtTheBeginingOfPeriod, userId))
          .sumOfAllFundsAtTheEndOfPeriod(calculateSumOfFunds(accountsStateAtTheEndOfPeriod, userId))
          .startDate(LocalDate.parse(transactionsInMonth.getKey()))
          .endDate(LocalDate.parse(transactionsInMonth.getKey()).plusMonths(1).minusDays(1))
          .transactions(transactionsInMonth.getValue())
          .build();

      transactionsInMonth.getValue().sort(Comparator.comparing(ExportTransaction::getDate));

      periods.add(period);

      // account state at the beginning of period is also account state at the end of the previous period (next we will process)
      accountsStateAtTheEndOfPeriod = accountsStateAtTheBeginingOfPeriod;
    }

    return periods;
  }

  private void subtractTransactionsValuesFromAccountStateToCalculateStateBeforeTransactions(
      Entry<String, List<ExportTransaction>> transactionsInMonth, List<ExportAccount> accountStateAtTheBeginingOfPeriod) {
    Map<String, ExportAccount> accountNameToAccountMap = calculateAccountNameToAccountMap(accountStateAtTheBeginingOfPeriod);

    // subtract transaction value from account state to calculate state before transaction
    for (ExportTransaction transaction : transactionsInMonth.getValue()) {
      for (ExportAccountPriceEntry entry : transaction.getAccountPriceEntries()) {
        ExportAccount account = accountNameToAccountMap.get(entry.getAccount());
        account.setBalance(account.getBalance().subtract(entry.getPrice()));
      }
    }
  }

  private Map<String, ExportAccount> calculateAccountNameToAccountMap(List<ExportAccount> accountStateAtTheBeginingOfPeriod) {
    Map<String, ExportAccount> accountNameToAccountMap = new HashMap<>();
    for (ExportAccount account : accountStateAtTheBeginingOfPeriod) {
      accountNameToAccountMap.put(account.getName(), account);
    }
    return accountNameToAccountMap;
  }

  private Map<String, List<ExportTransaction>> groupTransactionsByMonth(List<ExportTransaction> transactions) {
    Map<String, List<ExportTransaction>> monthToTransactionMap = new TreeMap<>(Collections.reverseOrder());

    for (ExportTransaction transaction : transactions) {
      String key = getKey(transaction.getDate());
      monthToTransactionMap.computeIfAbsent(key, input -> new ArrayList<>());
      monthToTransactionMap.get(key).add(transaction);
    }
    return monthToTransactionMap;
  }

  private List<ExportTransaction> convertTransactionsToExportTransactions(List<Transaction> transactions, long userId) {
    List<ExportTransaction> convertedTransactions = new ArrayList<>();

    for (Transaction transaction : transactions) {
      ExportTransaction exportTransaction = ExportTransaction.builder()
          .description(transaction.getDescription())
          .date(transaction.getDate())
          .accountPriceEntries(new ArrayList<>())
          .category(categoryService.getCategoryByIdAndUserId(transaction.getCategoryId(), userId).orElse(new Category()).getName())
          .build();

      for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
        exportTransaction.getAccountPriceEntries().add(
            ExportAccountPriceEntry.builder()
                .account(accountService.getAccountByIdAndUserId(entry.getAccountId(), userId).orElse(new Account()).getName())
                .price(entry.getPrice())
                .build()
        );
      }

      convertedTransactions.add(exportTransaction);
    }

    return convertedTransactions;
  }

  private List<ExportCategory> prepareExportCategories(long userId) {
    return categoryService.getCategories(userId)
        .stream()
        .map(category -> ExportCategory.builder()
            .name(category.getName())
            .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
            .build()
        )
        .collect(Collectors.toList());
  }

  private List<ExportFilter> prepareExportFilters(long userId) {
    return filterService.getAllFilters(userId)
        .stream()
        .map(filter -> ExportFilter.builder()
            .name(filter.getName())
            .priceFrom(filter.getPriceFrom())
            .priceTo(filter.getPriceTo())
            .dateFrom(filter.getDateFrom())
            .dateTo(filter.getDateTo())
            .description(filter.getDescription())
            .accounts(filter.getAccountIds().stream()
                .map(accountId -> accountService.getAccountByIdAndUserId(accountId, userId).orElse(new Account()).getName())
                .collect(Collectors.toList()))
            .categories(filter.getCategoryIds().stream()
                .map(categoryId -> categoryService.getCategoryByIdAndUserId(categoryId, userId).orElse(new Category()).getName())
                .collect(Collectors.toList()))
            .build()

        )
        .collect(Collectors.toList());
  }

  private List<ExportAccount> convertToExportAccounts(List<Account> accounts) {
    return accounts.stream()
        .map(account -> ExportAccount.builder()
            .name(account.getName())
            .balance(account.getBalance())
            .currency(account.getCurrency().getName())
            .lastVerificationDate(account.getLastVerificationDate())
            .archived(account.isArchived())
            .build()
        )
        .collect(Collectors.toList());
  }

  private List<ExportAccount> copyAccounts(List<ExportAccount> accounts) {
    return accounts.stream()
        .map(account -> ExportAccount.builder()
            .balance(account.getBalance())
            .name(account.getName())
            .currency(account.getCurrency())
            .build()
        )
        .collect(Collectors.toList());
  }

  private String getKey(LocalDate date) {
    return String.format("%04d-%02d-01", date.getYear(), date.getMonth().getValue());
  }

  private ExportFundsSummary calculateSumOfFunds(List<ExportAccount> accounts, long userId) {
    List<Currency> currencies = currencyService.getCurrencies(userId);

    // ENHANCEMENT change to stream
    Map<String, BigDecimal> currencyToExchangeRate = new HashMap<>();
    Map<String, BigDecimal> currencyToBalanceMap = new HashMap<>();
    for (Currency currency : currencies) {
      currencyToExchangeRate.put(currency.getName(), currency.getExchangeRate());
      currencyToBalanceMap.put(currency.getName(), BigDecimal.ZERO);
    }

    BigDecimal sumOfAllFunds = accounts.stream()
        .map(account -> account.getBalance().multiply(currencyToExchangeRate.get(account.getCurrency())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    for (ExportAccount account : accounts) {
      currencyToBalanceMap.put(account.getCurrency(), currencyToBalanceMap.get(account.getCurrency()).add(account.getBalance()));
    }

    return ExportFundsSummary.builder()
        .sumOfAllFundsInBaseCurrency(sumOfAllFunds)
        .currencyToFundsMap(currencyToBalanceMap)
        .build();
  }

}
