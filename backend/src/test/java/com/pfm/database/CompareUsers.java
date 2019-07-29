package com.pfm.database;

import static com.pfm.database.query.result.SqlTestQueriesProvider.GROUP_BY;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_ACCOUNTS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_CATEGORIES;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_CURRENCIES;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_FILTERS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_HISTORY;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_TRANSACTIONS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_MAIN_PARENT_CATEGORY_CATEGORIES;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pfm.database.query.result.AccountQueryResult;
import com.pfm.database.query.result.CategoryFromMainParentCategoryQueryResult;
import com.pfm.database.query.result.CategoryQueryResult;
import com.pfm.database.query.result.CurrencyQueryResult;
import com.pfm.database.query.result.FilterQueryResult;
import com.pfm.database.query.result.HistoryQueryResult;
import com.pfm.database.query.result.TransactionQueryResult;
import com.pfm.database.row.mappers.AccountQueryResultMapper;
import com.pfm.database.row.mappers.CategoryFromMainParentCategoryQueryResultMapper;
import com.pfm.database.row.mappers.CategoryQueryResultMapper;
import com.pfm.database.row.mappers.CurrencyQueryResultMapper;
import com.pfm.database.row.mappers.FilterQueryResultRowMapper;
import com.pfm.database.row.mappers.HistoryQueryResultMapper;
import com.pfm.database.row.mappers.TransactionQueryResultMapper;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class CompareUsers {
  // This test assumes that you have Postgre SQL database installed in your machine, and the DB server is on
  // the test is disabled to make build pass
  @Disabled
  @Test
  void shouldCompareTablesForGivenUsersIDsInDatabase() {

    final long user_1_Id = 1L;
    final long user_2_Id = 2L;

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://localhost:5432/pfm");
    dataSource.setUsername("postgres");
    dataSource.setPassword("1234");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final List<AccountQueryResult> user1Accounts = getAccountsFromDb(user_1_Id, jdbcTemplate);
    final List<AccountQueryResult> user2Accounts = getAccountsFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1Accounts, user2Accounts);

    final List<CurrencyQueryResult> user1Currencies = getCurrenciesFromDb(user_1_Id, jdbcTemplate);
    final List<CurrencyQueryResult> user2Currencies = getCurrenciesFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1Currencies, user2Currencies);

    final List<HistoryQueryResult> user1History = getHistoryFromDb(user_1_Id, jdbcTemplate);
    final List<HistoryQueryResult> user2History = getHistoryFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1History, user2History);

    final List<TransactionQueryResult> user1Transactions = getTransactionFromDb(user_1_Id, jdbcTemplate);
    final List<TransactionQueryResult> user2Transactions = getTransactionFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1Transactions, user2Transactions);

    final List<CategoryQueryResult> user1Categories = getCategoryFromDb(user_1_Id, jdbcTemplate);
    final List<CategoryQueryResult> user2Categories = getCategoryFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1Categories, user2Categories);

    final List<CategoryFromMainParentCategoryQueryResult> user1MainCategoryCategories = getCategoriesFromMainCategoryFromDb(user_1_Id, jdbcTemplate);
    final List<CategoryFromMainParentCategoryQueryResult> user2MainCategoryCategories = getCategoriesFromMainCategoryFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1MainCategoryCategories, user2MainCategoryCategories);

    final List<FilterQueryResult> user1Filters = getFiltersFromDb(user_1_Id, jdbcTemplate);
    final List<FilterQueryResult> user2Filters = getFiltersFromDb(user_2_Id, jdbcTemplate);

    assertEquals(user1Filters, user2Filters);
  }

  private List<AccountQueryResult> getAccountsFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_ACCOUNTS + userId + GROUP_BY, new AccountQueryResultMapper());
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

