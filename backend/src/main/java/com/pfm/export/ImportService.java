package com.pfm.export;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class ImportService {

  private TransactionService transactionService;
  private AccountService accountService;
  private CategoryService categoryService;
  private CurrencyService currencyService;

  @Transactional
  void importData(@RequestBody ExportResult inputData, long userId) throws ImportFailedException {
    Map<String, Long> categoryNameToIdMap = importCategoriesAndMapCategoryNamesToIds(inputData, userId);
    Map<String, Long> accountNameToIdMap = importAccountsAndMapAccountNamesToIds(inputData, userId);

    for (ExportPeriod period : inputData.getPeriods()) {
      for (ExportTransaction transaction : period.getTransactions()) {
        importTransaction(categoryNameToIdMap, accountNameToIdMap, transaction, userId);
      }
    }

  }

  // ENHANCEMENT add checking account state during import based on period start and end balances & overall account states
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

    transactionService.addTransaction(userId, newTransaction);
  }

  private Map<String, Long> importAccountsAndMapAccountNamesToIds(@RequestBody ExportResult inputData, long userId) throws ImportFailedException {
    List<Currency> currencies = currencyService.getCurrencies(userId); // ENHANCEMENT can be replaced with HashMap

    Map<String, Long> accountNameToIdMap = new HashMap<>();
    for (ExportAccount account : inputData.getInitialAccountsState()) {
      if (account.getCurrency() == null) { // backward compatibility - set default currency
        account.setCurrency("PLN");
      }

      Optional<Currency> currencyOptional = currencies.stream().filter(currency -> currency.getName().equals(account.getCurrency())).findAny();

      if (currencyOptional.isEmpty()) {
        throw new ImportFailedException(String.format(getMessage(ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST), account.getCurrency()));
      }

      Account accountToSave = Account.builder()
          .name(account.getName())
          .balance(account.getBalance())
          .currency(currencyOptional.get())
          .build();

      Account savedAccount = accountService.addAccount(userId, accountToSave);
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
