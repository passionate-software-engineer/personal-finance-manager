package com.pfm.export;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_NAME_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeService;
import com.pfm.auth.UserProvider;
import com.pfm.category.Category;
import com.pfm.category.CategoryRepository;
import com.pfm.category.CategoryService;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterService;
import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryEntryRepository;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Getter
@AllArgsConstructor
public class ImportService {

  public static final String CATEGORY_NAMED_IMPORTED = "Imported";
  public static final String MORE_THAN_ONE_CATEGORY_NAMED_IMPORTED_FOUND = "More than one category with name " + CATEGORY_NAMED_IMPORTED + " found";
  public static final int CATEGORY_NAMED_IMPORTED_COUNT_ALLOWED = 1;

  private CategoryService categoryService;
  private CurrencyService currencyService;
  private AccountService accountService;
  private AccountTypeService accountTypeService;
  private FilterService filterService;
  private TransactionService transactionService;
  private CategoryRepository categoryRepository;
  private HistoryEntryRepository historyEntryRepository;
  private UserProvider userProvider;

  @Transactional
  void importData(@RequestBody ExportResult inputData, long userId) throws ImportFailedException {
    Map<String, Long> categoryNameToIdMap = importCategoriesAndMapCategoryNamesToIds(inputData, userId);
    Map<String, Long> accountNameToIdMap = importAccountsAndMapAccountNamesToIds(inputData, userId);
    importFilters(inputData, userId, accountNameToIdMap, categoryNameToIdMap);

    for (ExportPeriod period : inputData.getPeriods()) {
      for (ExportTransaction transaction : period.getTransactions()) {
        importTransaction(categoryNameToIdMap, accountNameToIdMap, transaction, userId);
      }
    }

    for (HistoryEntry historyEntry : inputData.getHistoryEntries()) {
      historyEntry.setUserId(userProvider.getCurrentUserId());
      saveHistoryEntry(historyEntry);
    }

  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void importFilters(ExportResult inputData, long userId, Map<String, Long> accountNameToIdMap, Map<String, Long> categoryNameToIdMap) {
    for (ExportFilter importedFilter : inputData.getFilters()) {
      Filter filter = new Filter();
      filter.setName(importedFilter.getName());
      filter.setDateFrom(importedFilter.getDateFrom());
      filter.setDateTo(importedFilter.getDateTo());
      filter.setDescription(importedFilter.getDescription());
      filter.setPriceFrom(importedFilter.getPriceFrom());
      filter.setPriceTo(importedFilter.getPriceTo());
      if (importedFilter.getAccounts() != null) {
        filter.setAccountIds(importedFilter.getAccounts().stream()
            .map(accountNameToIdMap::get)
            .collect(Collectors.toList()));
      }
      if (importedFilter.getCategories() != null) {
        filter.setCategoryIds(importedFilter.getCategories().stream()
            .map(categoryNameToIdMap::get)
            .collect(Collectors.toList()));
      }

      filterService.addFilter(userId, filter);
    }
  }

  // TODO add checking account state during import based on period start and end balances & overall account states
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

    transactionService.addTransaction(userId, newTransaction, true);
  }

  private Map<String, Long> importAccountsAndMapAccountNamesToIds(@RequestBody ExportResult inputData, long userId) throws ImportFailedException {
    Map<String, Currency> currencyMap = currencyService.getCurrencies(userId).stream()
        .collect(Collectors.toMap(Currency::getName, currency -> currency));

    Map<String, AccountType> accountTypeMap = accountTypeService.getAccountTypes(userId).stream()
        .collect(Collectors.toMap(AccountType::getName, accountType -> accountType));

    Map<String, Long> accountNameToIdMap = new HashMap<>();
    for (ExportAccount account : inputData.getInitialAccountsState()) {
      if (account.getCurrency() == null) { // backward compatibility - set default currency
        account.setCurrency("PLN");
      }
      if (account.getAccountType() == null) { // backward compatibility - set default type
        account.setAccountType("Personal");
      }

      Currency currency = currencyMap.get(account.getCurrency());

      if (currency == null) {
        throw new ImportFailedException(String.format(getMessage(ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST), account.getCurrency()));
      }

      AccountType accountType = accountTypeMap.get(account.getAccountType());

      if (accountType == null) {
        throw new ImportFailedException(String.format(getMessage(ACCOUNT_TYPE_NAME_DOES_NOT_EXIST), account.getCurrency()));
      }

      Account accountToSave = Account.builder()
          .name(account.getName())
          .bankAccountNumber(account.getBankAccountNumber())
          .balance(account.getBalance())
          .currency(currency)
          .type(accountType)
          .lastVerificationDate(account.getLastVerificationDate())
          .archived(account.isArchived())
          .build();

      Account savedAccount = accountService.saveAccount(userId, accountToSave);
      accountNameToIdMap.put(savedAccount.getName(), savedAccount.getId());
    }
    return accountNameToIdMap;
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private Map<String, Long> importCategoriesAndMapCategoryNamesToIds(@RequestBody ExportResult inputData, long userId) {
    Map<String, Long> categoryNameToIdMap = new HashMap<>();

    for (ExportCategory category : inputData.getCategories()) {
      Category categoryToSave = new Category();
      categoryToSave.setName(category.getName());
      categoryToSave.setPriority(category.getPriority());
      categoryToSave.setUserId(userId);
      if (category.getParentCategoryName() == null) {
        if (category.getName().equals(CATEGORY_NAMED_IMPORTED)) {
          deleteCategoryNamedImportedFromDbIfAlreadyExistToPreventDuplication(userId);
        }
      } else {
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

  void deleteCategoryNamedImportedFromDbIfAlreadyExistToPreventDuplication(long userId) {
    final List<Category> importedCategoryFromDbList = categoryRepository.findByNameIgnoreCaseAndUserId(CATEGORY_NAMED_IMPORTED, userId);
    long categoriesWithNameImportedCount = importedCategoryFromDbList.size();
    if (categoriesWithNameImportedCount > CATEGORY_NAMED_IMPORTED_COUNT_ALLOWED) {
      throw new IllegalStateException(MORE_THAN_ONE_CATEGORY_NAMED_IMPORTED_FOUND);
    }
    categoryService.deleteCategory(importedCategoryFromDbList.get(0).getId());
  }

  private void saveHistoryEntry(HistoryEntry historyEntry) {
    historyEntryRepository.save(historyEntry);
  }
}
