package com.pfm.database;

import static com.pfm.helpers.TestSqlQueryProvider.SELECT_ACCOUNT_FOR_USER_NO_4;
import static com.pfm.helpers.TestSqlQueryProvider.SELECT_ALL_ACCOUNTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.AccountService;
import com.pfm.auth.User;
import com.pfm.currency.Currency;
import com.pfm.export.ExportResult;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestUsersProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
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

  private static final String JSON_TEST_DATA_SOURCE_FILE_PATH = "src/test/resources/databaseIntegrationTestDataSource.json";
  //  private static final String JSON_TEST_DATA_SOURCE_FILE_PATH = "src/test/resources/dd.json";

  @Autowired
  AccountService accountService;
  @Autowired
  DataSource dataSource;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Qualifier("pfmObjectMapper")
  @Autowired
  ObjectMapper mapper;

//  @BeforeEach
//  public void before() {
//    flyway.clean();
//    flyway.migrate();
//  }

  @Disabled
  @Test
  void importExportCrossTest() throws Exception {
    //given
    File jsonTestDataFile = new File(JSON_TEST_DATA_SOURCE_FILE_PATH);

    List<String> exportResultJson = Files.lines(Paths.get(JSON_TEST_DATA_SOURCE_FILE_PATH))
        .collect(Collectors.toList());

    ExportResult dataToImportByUser1 = mapper.readValue(jsonTestDataFile, ExportResult.class);
    assertNotNull(dataToImportByUser1);

    User user1 = TestUsersProvider.userMarian();
    long user1Id = callRestToRegisterUserAndReturnUserId(user1);
    String user1Token = callRestToAuthenticateUserAndReturnToken(user1);
    callRestToImportAllData(user1Token, dataToImportByUser1);
    ExportResult dataExportedBackByUser1 = callRestToExportAllDataAndReturnExportResult(user1Token);

    User user2 = TestUsersProvider.userZdzislaw();
    long user2Id = callRestToRegisterUserAndReturnUserId(user2);
    String user2Token = callRestToAuthenticateUserAndReturnToken(user2);
    callRestToImportAllData(user2Token, dataExportedBackByUser1);
    ExportResult dataExportedBackByUser2 = callRestToExportAllDataAndReturnExportResult(user2Token);

    assertEquals(json(dataToImportByUser1), json(dataExportedBackByUser1));
    assertEquals(json(dataToImportByUser1), json(dataExportedBackByUser2));
    assertThat(json(dataExportedBackByUser1), equalTo(json(dataExportedBackByUser2)));

    assertEquals(dataToImportByUser1, dataExportedBackByUser1);
    assertEquals(dataToImportByUser1, dataExportedBackByUser2);
    assertThat(dataExportedBackByUser1, equalTo(dataExportedBackByUser2));

  }

  @Test
  void shouldCompareTablesInDatabase() throws Exception {
    //given
    File jsonTestDataFile = new File(JSON_TEST_DATA_SOURCE_FILE_PATH);

    List<String> exportResultJson = Files.lines(Paths.get(JSON_TEST_DATA_SOURCE_FILE_PATH))
        .collect(Collectors.toList());

    ExportResult dataToImportByUser1 = mapper.readValue(jsonTestDataFile, ExportResult.class);
    assertNotNull(dataToImportByUser1);

    User user1 = TestUsersProvider.userMarian();
    long user1Id = callRestToRegisterUserAndReturnUserId(user1);
    String user1Token = callRestToAuthenticateUserAndReturnToken(user1);
    callRestToImportAllData(user1Token, dataToImportByUser1);
    ExportResult dataExportedBackByUser1 = callRestToExportAllDataAndReturnExportResult(user1Token);

    User user2 = TestUsersProvider.userZdzislaw();
    long user2Id = callRestToRegisterUserAndReturnUserId(user2);
    String user2Token = callRestToAuthenticateUserAndReturnToken(user2);
    callRestToImportAllData(user2Token, dataExportedBackByUser1);
    ExportResult dataExportedBackByUser2 = callRestToExportAllDataAndReturnExportResult(user2Token);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

//    List<AccountQueryResult> accountsFromDb = getAccountForUserNo_4();
//    printResults(accountsFromDb);

    printCurrency(getCurrency());
//    System.out.println("*****************currency **********************************************************************************************");

    List<AccountQueryResult> diffrenceInAccounts = compareAccountTables();
    printResults(diffrenceInAccounts);

    System.out.println("***************************************************************************************************************");
    System.out.println("***************************************************************************************************************");

  }

  private String getAccountById(long id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    String sql = SELECT_ALL_ACCOUNTS;

    String account = (String) jdbcTemplate.queryForObject(
        sql, new Object[]{id}, String.class);
    return account;
  }

  private void printResults(List<AccountQueryResult> results) {
//    results.forEach(System.out::println);
    for (AccountQueryResult result : results) {
      System.out.print(result.toString() + " ");
    }

  }

  private void printCurrency(List<Currency> results) {
//    results.forEach(System.out::println);
    for (Currency result : results) {
      System.out.print(result.toString() + " ");
      System.out.println("*********************************Currency************************************************************************************");
    }

  }
  private List<Currency> getCurrency() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    String sql = "select * from currency";
    return  jdbcTemplate.query(sql, new CurrencyRowMapper());

  }


  private List<AccountQueryResult> compareAccountTables() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    String sql = "select * from account";

    return jdbcTemplate.query(sql, new AccountQueryResultMapper());
  }
  private List<AccountQueryResult> getAccountForUserNo_4() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    String sql = SELECT_ACCOUNT_FOR_USER_NO_4;

    List<AccountQueryResult> resultAsString = jdbcTemplate.query(sql, new AccountQueryResultMapper());
    return resultAsString;
  }

//  private String getAccountByIdd(long userId, long accountId) {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//    String sql = "SELECT account.name from account where user_id=:userId and account.id=:accountId";
////    String sql = "SELECT username from app_user where app_user.id=? ";
//
//    String account = (String) jdbcTemplate.queryForObject(
//        sql, new Object[]{accountId}, String.class);
//
//    return account;
//  }
}

