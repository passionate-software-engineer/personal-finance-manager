package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
    result.setCategories(categoryService.getCategories());

    Map<String, Set<Transaction>> monthToTransactionMap = new TreeMap<>(Collections.reverseOrder());

    for (Transaction transaction : transactionService.getTransactions()) {
      String key = getKey(transaction.getDate());

      monthToTransactionMap.computeIfAbsent(key, k -> new HashSet<>());

      monthToTransactionMap.get(key).add(transaction);
    }

    result.setFinalAccountsState(copyAccounts(accountService.getAccounts()));

    List<Account> accountsStateAtTheEndOfPeriod = copyAccounts(accountService.getAccounts());

    for (Entry<String, Set<Transaction>> transactionsInMonth : monthToTransactionMap.entrySet()) {

      List<Account> accounts = copyAccounts(accountsStateAtTheEndOfPeriod);

      for (Transaction transaction : transactionsInMonth.getValue()) {
        for (Account account : accounts) { // TODO replace with faster Hashmap
          if (transaction.getAccountId() == account.getId()) {
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

      result.getPeriods().add(period);

      accountsStateAtTheEndOfPeriod = copyAccounts(accounts);
    }

    result.setInitialAccountsState(accountsStateAtTheEndOfPeriod);

    return result;
  }

  @Override
  public void importData(@RequestBody ExportResult inputData) {
    for (Category category : inputData.getCategories()) {
      categoryService.addCategory(category);
    }

    for (Account account : inputData.getInitialAccountsState()) {
      accountService.addAccount(account);
    }

    for (ExportPeriod period : inputData.getPeriods()) {
      for (Transaction transaction : period.getTransactions()) {
        transaction.setId(null);
        transactionService.addTransaction(transaction);
      }
    }
  }

  private List<Account> copyAccounts(List<Account> accounts) {
    return accounts.stream()
        .map(account -> Account.builder()
            .id(account.getId())
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
