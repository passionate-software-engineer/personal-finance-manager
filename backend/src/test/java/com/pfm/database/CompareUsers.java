package com.pfm.database;

import static com.pfm.database.query.result.SqlTestQueriesProvider.GROUP_BY;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_ACCOUNTS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_CATEGORIES;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_CURRENCIES;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_FILTERS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_HISTORY;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_ALL_TRANSACTIONS;
import static com.pfm.database.query.result.SqlTestQueriesProvider.SELECT_MAIN_PARENT_CATEGORY_CATEGORIES;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.category.Category;
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
import com.pfm.filter.Filter;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.transaction.Transaction;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
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

  @Qualifier("pfmObjectMapper")
  @Autowired
  ObjectMapper mapper;

  @Autowired
  DataSource dataSource;

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldCompareDatabaseTablesOfUserMarianAndUserZdzislaw() throws Exception {
    // given
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    final long userMarianId = userId;

    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(2)); // PLN
    String userMarianToken = token;

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, userMarianToken);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), userMarianToken);
    callRestToAddCategoryAndReturnId(Category.builder()
        .name("Pizza")
        .parentCategory(Category.builder()
            .id(foodCategoryId)
            .build()
        ).build(), userMarianToken);

    Transaction transactionToAddFood = foodTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transactionToAddFood, jacekAccountId, foodCategoryId, userMarianToken);

    Filter filter = filterFoodExpenses();
    filter.getCategoryIds().add(foodCategoryId);
    filter.getAccountIds().add(jacekAccountId);
    callRestServiceToAddFilterAndReturnId(filter, userMarianToken);
    ExportResult exportedData = callRestToExportAllDataAndReturnExportResult(userMarianToken);

    long userZdzislawId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String userZdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    //when
    callRestToImportAllData(userZdzislawToken, exportedData);

    //then
    final List<AccountQueryResult> user1Accounts = getAccountsFromDb(userMarianId, jdbcTemplate);
    final List<AccountQueryResult> user2Accounts = getAccountsFromDb(userZdzislawId, jdbcTemplate);

    assertEquals(user1Accounts, user2Accounts);

    final List<CurrencyQueryResult> user1Currencies = getCurrenciesFromDb(userMarianId, jdbcTemplate);
    final List<CurrencyQueryResult> user2Currencies = getCurrenciesFromDb(userZdzislawId, jdbcTemplate);

    assertEquals(user1Currencies, user2Currencies);

    final List<HistoryQueryResult> user1History = getHistoryFromDb(userMarianId, jdbcTemplate);
    final List<HistoryQueryResult> user2History = getHistoryFromDb(userZdzislawId, jdbcTemplate);

    assertEquals(user1History, user2History);

    final List<TransactionQueryResult> user1Transactions = getTransactionFromDb(userMarianId, jdbcTemplate);
    final List<TransactionQueryResult> user2Transactions = getTransactionFromDb(userZdzislawId, jdbcTemplate);

    assertEquals(user1Transactions, user2Transactions);

    final List<CategoryQueryResult> user1Categories = getCategoryFromDb(userMarianId, jdbcTemplate);
    final List<CategoryQueryResult> user2Categories = getCategoryFromDb(userZdzislawId, jdbcTemplate);

    assertEquals(user1Categories, user2Categories);

    final List<CategoryFromMainParentCategoryQueryResult> user1MainCategoryCategories = getCategoriesFromMainCategoryFromDb(userMarianId,
        jdbcTemplate);
    final List<CategoryFromMainParentCategoryQueryResult> user2MainCategoryCategories = getCategoriesFromMainCategoryFromDb(userZdzislawId,
        jdbcTemplate);

    assertEquals(user1MainCategoryCategories, user2MainCategoryCategories);

    final List<FilterQueryResult> user1Filters = getFiltersFromDb(userMarianId, jdbcTemplate);
    final List<FilterQueryResult> user2Filters = getFiltersFromDb(userZdzislawId, jdbcTemplate);

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
