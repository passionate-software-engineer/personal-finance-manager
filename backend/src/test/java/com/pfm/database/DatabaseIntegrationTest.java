package com.pfm.database;

import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_ACCOUNTS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_CATEGORIES;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_CURRENCIES;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_FILTERS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_HISTORY;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_TRANSACTIONS;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_MAIN_PARENT_CATEGORY_CATEGORIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.AccountService;
import com.pfm.auth.User;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DatabaseIntegrationTest extends IntegrationTestsBase {

  private static final String JSON_TEST_DATA_FILE_PATH1 = "src/test/resources/databaseIntegrationTestDataSourceForUser1.json";
  private static final String JSON_TEST_DATA_FILE_PATH2 = "src/test/resources/databaseIntegrationTestDataSourceForUser2.json";

  @Autowired
  AccountService accountService;
  @Autowired
  DataSource dataSource;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Qualifier("pfmObjectMapper")
  @Autowired
  ObjectMapper mapper;

  @Disabled
  @Test
  void shouldCompareTablesInDatabase() throws Exception {
    //given
    File testDataFileForUser1 = new File(JSON_TEST_DATA_FILE_PATH1);
    File testDataFileForUser2 = new File(JSON_TEST_DATA_FILE_PATH2);

    ExportResult dataToImportFromFileByUser1 = mapper.readValue(testDataFileForUser1, ExportResult.class);
    ExportResult dataToImportFromFileByUser2 = mapper.readValue(testDataFileForUser2, ExportResult.class);

    User user1 = TestUsersProvider.userMarian();
    final long user1Id = callRestToRegisterUserAndReturnUserId(user1);
    String user1Token = callRestToAuthenticateUserAndReturnToken(user1);
    callRestToImportAllData(user1Token, dataToImportFromFileByUser1);
    ExportResult dataExportedBackByUser1 = callRestToExportAllDataAndReturnExportResult(user1Token);

    User user2 = TestUsersProvider.userZdzislaw();
    final long user2Id = callRestToRegisterUserAndReturnUserId(user2);
    String user2Token = callRestToAuthenticateUserAndReturnToken(user2);
    callRestToImportAllData(user2Token, dataToImportFromFileByUser2);
    ExportResult dataExportedBackByUser2 = callRestToExportAllDataAndReturnExportResult(user2Token);

    assertNotNull(dataToImportFromFileByUser1);
    assertNotNull(dataExportedBackByUser1);
    assertNotEquals(dataExportedBackByUser1, dataExportedBackByUser2);

    final List<AccountQueryResult> user1AccountQueryResults = getAccountsFromDb(user1Id);
    final List<AccountQueryResult> user2AccountQueryResults = getAccountsFromDb(user2Id);

    final List<CurrencyQueryResult> user1CurrenciesQueryResults = getCurrenciesFromDb(user1Id);
    final List<CurrencyQueryResult> user2CurrenciesQueryResults = getCurrenciesFromDb(user2Id);

    final List<HistoryQueryResult> user1HistoryQueryResults = getHistoryFromDb(user1Id);
    final List<HistoryQueryResult> user2HistoryQueryResults = getHistoryFromDb(user2Id);

    final List<TransactionQueryResult> user1TransactionQueryResults = getTransactionFromDb(user1Id);
    final List<TransactionQueryResult> user2TransactionQueryResults = getTransactionFromDb(user2Id);

    final List<CategoryQueryResult> user1CategoryQueryResults = getCategoryFromDb(user1Id);
    final List<CategoryQueryResult> user2CategoryQueryResults = getCategoryFromDb(user2Id);

    final List<CategoryFromMainParentCategoryQueryResult> user1MainParentCategoryCategoriesQueryResults = getCategoriesFromMainCategoryFromDb(
        user1Id);
    final List<CategoryFromMainParentCategoryQueryResult> user2MainParentCategoryCategoriesQueryResults = getCategoriesFromMainCategoryFromDb(
        user2Id);

    final List<FilterQueryResult> user1FilterQueryResults = getFiltersFromDb(user1Id);
    final List<FilterQueryResult> user2FilterQueryResults = getFiltersFromDb(user2Id);

    //then
    assertEquals(user1AccountQueryResults, user2AccountQueryResults);;
    assertEquals(user1CurrenciesQueryResults, user2CurrenciesQueryResults);
    assertEquals(user1HistoryQueryResults, user2HistoryQueryResults);
    assertEquals(user1TransactionQueryResults, user2TransactionQueryResults);
    assertEquals(user1CategoryQueryResults, user2CategoryQueryResults);
    assertEquals(user1MainParentCategoryCategoriesQueryResults, user2MainParentCategoryCategoriesQueryResults);
    assertEquals(user1FilterQueryResults, user2FilterQueryResults);

  }

  private List<AccountQueryResult> getAccountsFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_ACCOUNTS + userId, new AccountQueryResultMapper());
  }

  private List<CurrencyQueryResult> getCurrenciesFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_CURRENCIES + userId, new CurrencyQueryResultMapper());
  }

  private List<HistoryQueryResult> getHistoryFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_HISTORY + userId, new HistoryQueryResultMapper());
  }

  private List<TransactionQueryResult> getTransactionFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_TRANSACTIONS + userId, new TransactionQueryResultMapper());
  }

  private List<CategoryQueryResult> getCategoryFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_CATEGORIES + userId, new CategoryQueryResultMapper());
  }

  private List<CategoryFromMainParentCategoryQueryResult> getCategoriesFromMainCategoryFromDb(long userId) {
    return jdbcTemplate.query(SELECT_MAIN_PARENT_CATEGORY_CATEGORIES + userId, new CategoryFromMainParentCategoryQueryResultMapper());
  }

  private List<FilterQueryResult> getFiltersFromDb(long userId) {
    return jdbcTemplate.query(SELECT_ALL_FILTERS + userId, new FilterQueryResultRowMapper());
  }
}



