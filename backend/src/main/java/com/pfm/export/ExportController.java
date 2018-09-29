package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class ExportController implements ExportApi {

  private TransactionService transactionService;
  private AccountService accountService;
  private CategoryService categoryService;

  @Override
  public ExportResult exportData() {
    ExportResult result = new ExportResult();
    result.setPeriods(new ArrayList<>());
    result.setCategories(categoryService.getCategories()); // TODO remove category id from RS

    Map<String, List<ExportTransaction>> monthToTransactionMap = new TreeMap<>(Collections.reverseOrder());

    for (Transaction transaction : transactionService.getTransactions()) {
      String key = getKey(transaction.getDate());

      monthToTransactionMap.computeIfAbsent(key, k -> new ArrayList<>());

      ExportTransaction exportTransaction = ExportTransaction.builder()
          .description(transaction.getDescription())
          .date(transaction.getDate())
          .accountPriceEntries(new ArrayList<>())
          .category(categoryService.getCategoryById(transaction.getCategoryId()).orElse(new Category()).getName())
          .build();

      for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
        exportTransaction.getAccountPriceEntries().add(
            ExportAccountPriceEntry.builder()
                .account(accountService.getAccountById(entry.getAccountId()).orElse(new Account()).getName())
                .price(entry.getPrice())
                .build()
        );
      }

      monthToTransactionMap.get(key).add(exportTransaction);
    }

    result.setFinalAccountsState(convertToExportAccounts(accountService.getAccounts()));

    List<ExportAccount> accountsStateAtTheEndOfPeriod = convertToExportAccounts(accountService.getAccounts());

    for (Entry<String, List<ExportTransaction>> transactionsInMonth : monthToTransactionMap.entrySet()) {

      List<ExportAccount> accounts = copyAccounts(accountsStateAtTheEndOfPeriod);

      // subtract transaction value from account state to calculate state before transaction
      for (ExportTransaction transaction : transactionsInMonth.getValue()) {
        for (ExportAccountPriceEntry entry : transaction.getAccountPriceEntries()) {
          for (ExportAccount account : accounts) { // TODO replace with faster Hashmap
            if (entry.getAccount().equals(account.getName())) {
              account.setBalance(account.getBalance().subtract(entry.getPrice()));
              break;
            }
          }
        }
      }

      ExportPeriod period = ExportPeriod.builder()
          .accountStateAtTheBeginingOfPeriod(accounts)
          .accountStateAtTheEndOfPeriod(accountsStateAtTheEndOfPeriod)
          .startDate(LocalDate.parse(transactionsInMonth.getKey()))
          .endDate(LocalDate.parse(transactionsInMonth.getKey()).plusMonths(1).minusDays(1))
          .transactions(transactionsInMonth.getValue())
          .build();

      transactionsInMonth.getValue().sort(Comparator.comparing(ExportTransaction::getDate));

      result.getPeriods().add(period);

      accountsStateAtTheEndOfPeriod = copyAccounts(accounts);
    }

    result.setInitialAccountsState(accountsStateAtTheEndOfPeriod);

    // TODO - export, import filters

    return result;
  }

  @Override
  public void importData(@RequestBody ExportResult inputData) {
    Map<String, Long> categoryNameToIdMap = new HashMap<>();

    for (Category category : inputData.getCategories()) {
      Category savedCategory = categoryService.addCategory(category);
      categoryNameToIdMap.put(savedCategory.getName(), savedCategory.getId());
    }

    Map<String, Long> accountNameToIdMap = new HashMap<>();
    for (ExportAccount account : inputData.getInitialAccountsState()) {
      Account accountToSave = Account.builder()
          .name(account.getName())
          .balance(account.getBalance())
          .build();

      Account savedAccount = accountService.addAccount(accountToSave);
      accountNameToIdMap.put(savedAccount.getName(), savedAccount.getId());
    }

    for (ExportPeriod period : inputData.getPeriods()) {
      for (ExportTransaction transaction : period.getTransactions()) {
        Transaction newTransaction = Transaction.builder()
            .description(transaction.getDescription())
            .accountPriceEntries(new ArrayList<>())
            .date(transaction.getDate())
            .categoryId(categoryNameToIdMap.get(transaction.getCategory()))
            .build();

        if (transaction.getAccountPriceEntries() != null) {
          for (ExportAccountPriceEntry entry : transaction.getAccountPriceEntries()) {
            newTransaction.getAccountPriceEntries().add(
                AccountPriceEntry.builder()
                    .accountId(accountNameToIdMap.get(entry.getAccount()))
                    .price(entry.getPrice())
                    .build()
            );
          }
        }

        if (transaction.getAccount() != null && transaction.getPrice() != null) { // TODO remove after migration
          newTransaction.getAccountPriceEntries().add(
              AccountPriceEntry.builder()
                  .accountId(accountNameToIdMap.get(transaction.getAccount()))
                  .price(transaction.getPrice())
                  .build()
          );
        }

        transactionService.addTransaction(newTransaction);
      }
    }

    // TODO add checking account state during import based on period start and end balances
  }

  private List<ExportAccount> convertToExportAccounts(List<Account> accounts) {
    return accounts.stream()
        .map(account -> ExportAccount.builder()
            .name(account.getName())
            .balance(account.getBalance())
            .build()
        )
        .collect(Collectors.toList());
  }

  private List<ExportAccount> copyAccounts(List<ExportAccount> accounts) {
    return accounts.stream()
        .map(account -> ExportAccount.builder()
            .name(account.getName())
            .balance(account.getBalance())
            .build()
        )
        .collect(Collectors.toList());
  }

  private String getKey(LocalDate date) {
    return String.format("%04d-%02d-01", date.getYear(), date.getMonth().getValue());
  }
}
