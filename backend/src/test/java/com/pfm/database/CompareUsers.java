package com.pfm.database;

import static com.pfm.database.SqlTestQueriesProvider.GROUP_BY;
import static com.pfm.database.SqlTestQueriesProvider.SELECT_ALL_ACCOUNTS;

import com.pfm.database.row.mappers.AccountQueryResultMapper;
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
    final long USER_B_ID = 2L;

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://localhost:5432/pfm");
    dataSource.setUsername("postgres");
    dataSource.setPassword("1234");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final List<AccountQueryResult> user1AccountQueryResults = getAccountsFromDb(USER_A_ID, jdbcTemplate);
    final List<AccountQueryResult> user2AccountQueryResults = getAccountsFromDb(USER_B_ID, jdbcTemplate);

    List<DiffResult> accountDiff = getAccountsDiff(user1AccountQueryResults, user2AccountQueryResults);
    printDiff(accountDiff);
  }

  private List<DiffResult> getAccountsDiff(List<AccountQueryResult> user1AccountQueryResults, List<AccountQueryResult> user2AccountQueryResults) {
    List<DiffResult> diffResults = new ArrayList<>();
    int user1QueryResultSize = user1AccountQueryResults.size();
    int user2QueryResultSize = user2AccountQueryResults.size();
    if (user1QueryResultSize > user2QueryResultSize) {
      for (int i = 0; i < user2QueryResultSize; i++) {
        diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
      }
      for (int i = user2QueryResultSize; i < user1QueryResultSize; i++) {
        diffResults.add(user1AccountQueryResults.get(i).diff(AccountQueryResult.empty()));
      }
    } else {
      for (int i = 0; i < user1QueryResultSize; i++) {
        diffResults.add(user1AccountQueryResults.get(i).diff(user2AccountQueryResults.get(i)));
      }
      for (int i = user1AccountQueryResults.size(); i < user2QueryResultSize; i++) {
        diffResults.add(AccountQueryResult.empty().diff(user2AccountQueryResults.get(i)));
      }
    }

    return diffResults;
  }

  private void printDiff(List<DiffResult> diffResults) {
    for (DiffResult diffResult : diffResults) {
      if (diffResult.toString().equals("")) {
        continue;
      }
      System.out.println(diffResult);
    }

  }

  private List<AccountQueryResult> getAccountsFromDb(long userId, JdbcTemplate jdbcTemplate) {
    return jdbcTemplate.query(SELECT_ALL_ACCOUNTS + userId + GROUP_BY, new AccountQueryResultMapper());
  }
//    private List<CurrencyQueryResult> getCurrenciesFromDb ( long userId, JdbcTemplate jdbcTemplate){
//      return jdbcTemplate.query(SELECT_ALL_CURRENCIES + userId, new CurrencyQueryResultMapper());
//    }
//
//  private List<HistoryQueryResult> getHistoryFromDb(long userId, JdbcTemplate jdbcTemplate) {
//    return jdbcTemplate.query(SELECT_ALL_HISTORY + userId, new HistoryQueryResultMapper());
//  }
//
//  private List<TransactionQueryResult> getTransactionFromDb(long userId, JdbcTemplate jdbcTemplate) {
//    return jdbcTemplate.query(SELECT_ALL_TRANSACTIONS + userId, new TransactionQueryResultMapper());
//  }
//
//  private List<CategoryQueryResult> getCategoryFromDb(long userId, JdbcTemplate jdbcTemplate) {
//    return jdbcTemplate.query(SELECT_ALL_CATEGORIES + userId, new CategoryQueryResultMapper());
//  }
//
//  private List<CategoryFromMainParentCategoryQueryResult> getCategoriesFromMainCategoryFromDb(long userId,
//      JdbcTemplate jdbcTemplate) {
//    return jdbcTemplate.query(SELECT_MAIN_PARENT_CATEGORY_CATEGORIES + userId, new CategoryFromMainParentCategoryQueryResultMapper());
//  }
//
//  private List<FilterQueryResult> getFiltersFromDb(long userId, JdbcTemplate jdbcTemplate) {
//    return jdbcTemplate.query(SELECT_ALL_FILTERS + userId, new FilterQueryResultRowMapper());

}
