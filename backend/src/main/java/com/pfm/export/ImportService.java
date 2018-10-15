package com.pfm.export;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.helpers.topology.Graph;
import com.pfm.helpers.topology.Graph.Node;
import com.pfm.helpers.topology.TopologicalSortProvider;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class ImportService {

  private TransactionService transactionService;
  private AccountService accountService;
  private CategoryService categoryService;

  void importData(@RequestBody ExportResult inputData, long userId) {
    Map<String, Long> categoryNameToIdMap = importCategoriesAndMapCategoryNamesToIds(inputData, userId);
    Map<String, Long> accountNameToIdMap = importAccountsAndMapAccountNamesToIds(inputData, userId);

    for (ExportPeriod period : inputData.getPeriods()) {
      for (ExportTransaction transaction : period.getTransactions()) {
        importTransaction(categoryNameToIdMap, accountNameToIdMap, transaction, userId);
      }
    }

  }

  // TODO [enhancement] add checking account state during import based on period start and end balances & overall account states
  private void importTransaction(Map<String, Long> categoryNameToIdMap, Map<String, Long> accountNameToIdMap, ExportTransaction transaction,
      long userId) {
    Transaction newTransaction = Transaction.builder()
        .description(transaction.getDescription())
        .accountPriceEntries(new ArrayList<>())
        .date(transaction.getDate())
        .categoryId(categoryNameToIdMap.get(transaction.getCategory()))
        .userId(userId)
        .build();

    for (ExportAccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      Long accountId = accountNameToIdMap.get(entry.getAccount());

      newTransaction.getAccountPriceEntries().add(
          AccountPriceEntry.builder()
              .accountId(accountId)
              .price(entry.getPrice())
              .build()
      );
    }

    transactionService.addTransaction(newTransaction);
  }

  private Map<String, Long> importAccountsAndMapAccountNamesToIds(@RequestBody ExportResult inputData, long userId) {
    Map<String, Long> accountNameToIdMap = new HashMap<>();
    for (ExportAccount account : inputData.getInitialAccountsState()) {
      Account accountToSave = Account.builder()
          .name(account.getName())
          .balance(account.getBalance())
          .userId(userId)
          .build();

      Account savedAccount = accountService.addAccount(accountToSave);
      accountNameToIdMap.put(savedAccount.getName(), savedAccount.getId());
    }
    return accountNameToIdMap;
  }

  private Map<String, Long> importCategoriesAndMapCategoryNamesToIds(@RequestBody ExportResult inputData, long userId) {
    Map<String, Long> categoryNameToIdMap = new HashMap<>();

    List<ExportCategory> categoriesSortedTopologically = sortCategoriesTopologically(inputData.getCategories());
    for (ExportCategory category : categoriesSortedTopologically) {
      Category categoryToSave = new Category();
      categoryToSave.setName(category.getName());
      categoryToSave.setUserId(userId);
      if (category.getParentCategoryName() != null) {
        categoryToSave.setParentCategory(Category.builder()
            .id(categoryNameToIdMap.get(category.getParentCategoryName()))
            .build()
        );
      }
      Category savedCategory = categoryService.addCategory(categoryToSave, userId);
      categoryNameToIdMap.put(savedCategory.getName(), savedCategory.getId());
    }
    return categoryNameToIdMap;
  }

  private List<ExportCategory> sortCategoriesTopologically(List<ExportCategory> categories) {
    Graph<ExportCategory> graph = new Graph<>();

    Map<String, Node<ExportCategory>> categoryNameToNodeMap = new HashMap<>();

    for (ExportCategory category : categories) {
      Node<ExportCategory> node = new Node<>(category);
      graph.addNode(node);
      categoryNameToNodeMap.put(category.getName(), node);
    }

    for (ExportCategory category : categories) {
      if (category.getParentCategoryName() != null) {
        categoryNameToNodeMap.get(category.getParentCategoryName()).addEdge(categoryNameToNodeMap.get(category.getName()));
      }
    }

    return TopologicalSortProvider.sort(graph).stream().map(Node::getObject).collect(Collectors.toList());
  }

}
