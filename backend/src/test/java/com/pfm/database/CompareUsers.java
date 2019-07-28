package com.pfm.database;

import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_ACCOUNTS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_CATEGORIES;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_CURRENCIES;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_FILTERS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_HISTORY;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_TRANSACTIONS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_MAIN_PARENT_CATEGORY_CATEGORIES;

import com.pfm.database.row.mappers.AccountQueryResultMapper;
import com.pfm.database.row.mappers.CategoryFromMainParentCategoryQueryResultMapper;
import com.pfm.database.row.mappers.CategoryQueryResultMapper;
import com.pfm.database.row.mappers.CurrencyQueryResultMapper;
import com.pfm.database.row.mappers.FilterQueryResultRowMapper;
import com.pfm.database.row.mappers.HistoryQueryResultMapper;
import com.pfm.database.row.mappers.TransactionQueryResultMapper;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
public class CompareUsers {

  private static final String JSON_TEST_DATA_FILE_PATH1 = "src/test/resources/databaseIntegrationTestDataSourceForUser1.json";
  private static final String JSON_TEST_DATA_FILE_PATH2 = "src/test/resources/databaseIntegrationTestDataSourceForUser2.json";

  @Test
  void shouldCompareTablesInDatabase() {

    final long USER_A_ID = 1L;
    final long USER_B_ID = 3L;

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://localhost:5432/pfm");
    dataSource.setUsername("postgres");
    dataSource.setPassword("1234");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final List<AccountQueryResult> user1AccountQueryResults = getAccountsFromDb(USER_A_ID, jdbcTemplate);
    final List<AccountQueryResult> user2AccountQueryResults = getAccountsFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> accountDiffResults = compareAccounts(user1AccountQueryResults, user2AccountQueryResults);
    printDiffResult(accountDiffResults);

    final List<CurrencyQueryResult> user1CurrenciesQueryResults = getCurrenciesFromDb(USER_A_ID, jdbcTemplate);
    final List<CurrencyQueryResult> user2CurrenciesQueryResults = getCurrenciesFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> currencyDiffResults = compareCurrencies(user1CurrenciesQueryResults, user2CurrenciesQueryResults);
    printDiffResult(currencyDiffResults);

    final List<HistoryQueryResult> user1HistoryQueryResults = getHistoryFromDb(USER_A_ID, jdbcTemplate);

    final List<HistoryQueryResult> user2HistoryQueryResults = getHistoryFromDb(USER_B_ID, jdbcTemplate);
    List<DiffResult> historyDiffResults = compareHistory(user1HistoryQueryResults, user2HistoryQueryResults);
    printDiffResult(historyDiffResults);

    final List<TransactionQueryResult> user1TransactionQueryResults = getTransactionFromDb(USER_A_ID, jdbcTemplate);
    final List<TransactionQueryResult> user2TransactionQueryResults = getTransactionFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> transactionDiffResults = compareTransaction(user1TransactionQueryResults, user2TransactionQueryResults);
    printDiffResult(transactionDiffResults);

    final List<CategoryQueryResult> user1CategoryQueryResults = getCategoryFromDb(USER_A_ID, jdbcTemplate);
    final List<CategoryQueryResult> user2CategoryQueryResults = getCategoryFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> categoryDiffResults = compareCategory(user1CategoryQueryResults, user2CategoryQueryResults);
    printDiffResult(categoryDiffResults);

    final List<CategoryFromMainParentCategoryQueryResult> user1MainParentCategoryCategoriesQueryResults = getCategoriesFromMainCategoryFromDb(
        USER_A_ID
        , jdbcTemplate);
    final List<CategoryFromMainParentCategoryQueryResult> user2MainParentCategoryCategoriesQueryResults = getCategoriesFromMainCategoryFromDb(
        USER_B_ID, jdbcTemplate);

    List<DiffResult> categoriesFromMainParentCategoryDiffResults = compareMainParentCategoryCategories(user1MainParentCategoryCategoriesQueryResults,
        user2MainParentCategoryCategoriesQueryResults);
    printDiffResult(categoriesFromMainParentCategoryDiffResults);

    final List<FilterQueryResult> user1FilterQueryResults = getFiltersFromDb(USER_A_ID, jdbcTemplate);
    final List<FilterQueryResult> user2FilterQueryResults = getFiltersFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> filterDiffResults = compareFilters(user1FilterQueryResults, user2FilterQueryResults);
    printDiffResult(filterDiffResults);

  }

  private List<DiffResult> compareAccounts(List<AccountQueryResult> user1AccountQueryResults, List<AccountQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
   //fixme what if lists differs in size
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<DiffResult> compareCurrencies(List<CurrencyQueryResult> user1AccountQueryResults, List<CurrencyQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<DiffResult> compareHistory(List<HistoryQueryResult> user1AccountQueryResults, List<HistoryQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<DiffResult> compareTransaction(List<TransactionQueryResult> user1AccountQueryResults,
      List<TransactionQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    int listSizeDifference = user1AccountQueryResults.size() - user2AccountQueryResults.size();
    if (listSizeDifference > 0) {
      for (int i = 0; i < listSizeDifference; i++) {

        user2AccountQueryResults.add(TransactionQueryResult.builder()
            .account("")
            .category("")
            .date("")
            .description("")
            .price("")
            .build());
      }
    } else if (listSizeDifference < 0) {
      user1AccountQueryResults.add(TransactionQueryResult.builder()
          .account("")
          .category("")
          .date("")
          .description("")
          .price("")
          .build());
    }

    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<DiffResult> compareCategory(List<CategoryQueryResult> user1AccountQueryResults, List<CategoryQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<DiffResult> compareMainParentCategoryCategories(List<CategoryFromMainParentCategoryQueryResult> user1AccountQueryResults,
      List<CategoryFromMainParentCategoryQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private void printDiffResult(List<DiffResult> diffResults) {
    if (diffResults.isEmpty()) {
      return;
    }

    for (DiffResult diffResult : diffResults) {
      if (diffResult.toString().equals("")) {
        continue;
      }
      System.out.println("***********************************************************************\n");
      System.out.println(diffResult);
      System.out.println("***********************************************************************");
    }
  }

  private List<DiffResult> compareFilters(List<FilterQueryResult> user1AccountQueryResults, List<FilterQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    for (int i = 0; i < user1AccountQueryResults.size(); i++) {
      diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
    }
    return diffResults;
  }

  private List<AccountQueryResult> getAccountsFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_ACCOUNTS + userId, new AccountQueryResultMapper());
  }

  private List<CurrencyQueryResult> getCurrenciesFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_CURRENCIES + userId, new CurrencyQueryResultMapper());
  }

  private List<HistoryQueryResult> getHistoryFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_HISTORY + userId, new HistoryQueryResultMapper());
  }

  private List<TransactionQueryResult> getTransactionFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_TRANSACTIONS + userId, new TransactionQueryResultMapper());
  }

  private List<CategoryQueryResult> getCategoryFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_CATEGORIES + userId, new CategoryQueryResultMapper());
  }

  private List<CategoryFromMainParentCategoryQueryResult> getCategoriesFromMainCategoryFromDb(long userId,
      JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_MAIN_PARENT_CATEGORY_CATEGORIES + userId, new CategoryFromMainParentCategoryQueryResultMapper());
  }

  private List<FilterQueryResult> getFiltersFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_FILTERS + userId, new FilterQueryResultRowMapper());
  }
}
