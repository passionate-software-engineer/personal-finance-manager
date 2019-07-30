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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.auth.User;
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
import com.pfm.export.ExportResult;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestUsersProvider;
import java.io.File;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CompareUsers extends IntegrationTestsBase {

  private static final String JSON_TEST_DATA_FILE_PATH = "src/test/resources/compare_users_test_file.json";

  @Qualifier("pfmObjectMapper")
  @Autowired
  ObjectMapper mapper;

  @Autowired
  DataSource dataSource;

  @Test
  void shouldCompareTablesForGivenUsersIDsInDatabase() throws Exception {

    File testDataFile = new File(JSON_TEST_DATA_FILE_PATH);

    final ExportResult testData = mapper.readValue(testDataFile, ExportResult.class);

    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    User user1 = TestUsersProvider.userMarian();
    final long user1Id = callRestToRegisterUserAndReturnUserId(user1);
    String user1Token = callRestToAuthenticateUserAndReturnToken(user1);
    callRestToImportAllData(user1Token, testData);
    ExportResult dataExportedBackByUser1 = callRestToExportAllDataAndReturnExportResult(user1Token);

    User user2 = TestUsersProvider.userZdzislaw();
    final long user2Id = callRestToRegisterUserAndReturnUserId(user2);
    String user2Token = callRestToAuthenticateUserAndReturnToken(user2);
    callRestToImportAllData(user2Token, dataExportedBackByUser1);
    ExportResult dataExportedBackByUser2 = callRestToExportAllDataAndReturnExportResult(user2Token);

    assertNotNull(testData);
    assertNotNull(dataExportedBackByUser1);
    assertEquals(dataExportedBackByUser1, dataExportedBackByUser2);

    final List<AccountQueryResult> user1Accounts = getAccountsFromDb(user1Id, jdbcTemplate);
    final List<AccountQueryResult> user2Accounts = getAccountsFromDb(user2Id, jdbcTemplate);

    assertEquals(user1Accounts, user2Accounts);

    final List<CurrencyQueryResult> user1Currencies = getCurrenciesFromDb(user1Id, jdbcTemplate);
    final List<CurrencyQueryResult> user2Currencies = getCurrenciesFromDb(user2Id, jdbcTemplate);

    assertEquals(user1Currencies, user2Currencies);

    final List<HistoryQueryResult> user1History = getHistoryFromDb(user1Id, jdbcTemplate);
    final List<HistoryQueryResult> user2History = getHistoryFromDb(user2Id, jdbcTemplate);

    assertEquals(user1History, user2History);

    final List<TransactionQueryResult> user1Transactions = getTransactionFromDb(user1Id, jdbcTemplate);
    final List<TransactionQueryResult> user2Transactions = getTransactionFromDb(user2Id, jdbcTemplate);

    assertEquals(user1Transactions, user2Transactions);

    final List<CategoryQueryResult> user1Categories = getCategoryFromDb(user1Id, jdbcTemplate);
    final List<CategoryQueryResult> user2Categories = getCategoryFromDb(user2Id, jdbcTemplate);

    assertEquals(user1Categories, user2Categories);

    final List<CategoryFromMainParentCategoryQueryResult> user1MainCategoryCategories = getCategoriesFromMainCategoryFromDb(user1Id, jdbcTemplate);
    final List<CategoryFromMainParentCategoryQueryResult> user2MainCategoryCategories = getCategoriesFromMainCategoryFromDb(user2Id, jdbcTemplate);

    assertEquals(user1MainCategoryCategories, user2MainCategoryCategories);

    final List<FilterQueryResult> user1Filters = getFiltersFromDb(user1Id, jdbcTemplate);
    final List<FilterQueryResult> user2Filters = getFiltersFromDb(user2Id, jdbcTemplate);

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
