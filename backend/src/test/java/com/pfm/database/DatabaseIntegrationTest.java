package com.pfm.database;

import static com.pfm.database.TestSQLQueries.SELECT_ALL_ACCOUNTS_WHERE_USER_ID;
import static com.pfm.database.TestSQLQueries.SELECT_ALL_HISTORY_WHERE_USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.AccountService;
import com.pfm.auth.User;
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

    List<AccountQueryResult> user1AccountQueryResults = getAllAccounts(user1Id);
    List<AccountQueryResult> user2AccountQueryResults = getAllAccounts(user2Id);

    assertThat(user1AccountQueryResults, equalTo(user2AccountQueryResults));

    System.out.println("***************************************************************************************************************");
    System.out.println("***************************************************************************************************************");
    List<HistoryQueryResult> user1HistoryQueryResults = getHistory(user1Id);
    List<HistoryQueryResult> user2HistoryQueryResults= getHistory(user2Id);

    assertThat(user1HistoryQueryResults, equalTo(user2HistoryQueryResults));

    System.out.println();
  }

  @SuppressWarnings("unchecked")
  private List<AccountQueryResult> getAllAccounts(long userId) {
    return jdbcTemplate.query(SELECT_ALL_ACCOUNTS_WHERE_USER_ID + userId, new AccountQueryResultMapper());
  }
  @SuppressWarnings("unchecked")
  private List<HistoryQueryResult> getHistory(long userId) {
    return jdbcTemplate.query(SELECT_ALL_HISTORY_WHERE_USER_ID + userId, new HistoryQueryResultMapper());
  }

}



