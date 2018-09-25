package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
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

      monthToTransactionMap.get(key).add(
          ExportTransaction.builder()
              .description(transaction.getDescription())
              .price(transaction.getPrice())
              .date(transaction.getDate())
              .account(accountService.getAccountById(transaction.getAccountId()).orElse(new Account()).getName())
              .category(categoryService.getCategoryById(transaction.getCategoryId()).orElse(new Category()).getName())
              .build()
      );
    }

    result.setFinalAccountsState(convertToExportAccounts(accountService.getAccounts()));

    List<ExportAccount> accountsStateAtTheEndOfPeriod = convertToExportAccounts(accountService.getAccounts());

    for (Entry<String, List<ExportTransaction>> transactionsInMonth : monthToTransactionMap.entrySet()) {

      List<ExportAccount> accounts = copyAccounts(accountsStateAtTheEndOfPeriod);

      for (ExportTransaction transaction : transactionsInMonth.getValue()) {
        for (ExportAccount account : accounts) { // TODO replace with faster Hashmap
          if (transaction.getAccount().equals(account.getName())) {
            account.setBalance(account.getBalance().subtract(transaction.getPrice()));
            break;
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
        transactionService.addTransaction(
            Transaction.builder()
                .description(transaction.getDescription())
                .price(transaction.getPrice())
                .date(transaction.getDate())
                .accountId(accountNameToIdMap.get(transaction.getAccount()))
                .categoryId(categoryNameToIdMap.get(transaction.getCategory()))
                .build()
        );
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
