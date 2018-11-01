package com.pfm.account.performance;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.auth.User;
import com.pfm.auth.UserDetails;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

// TODO imporove all those tests
// TODO those tests takes lots of time - run it separetly not as Unit tests
@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class InvoicePerformanceTestBase {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  static final int THREAD_COUNT = 24;

  private static final String ACCOUNTS_SERVICE_PATH = "http://localhost:%d/accounts";

  private static final String USERS_SERVICE_PATH = "http://localhost:%d/users";

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Rule
  public final ErrorCollector collector = new ErrorCollector();

  @Qualifier("pfmObjectMapper")
  @Autowired
  protected ObjectMapper mapper;

  final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());

  String token;

  @LocalServerPort
  private int port;

  private Account[] getAccounts() {

    return given()
        .when()
        .header("Authorization", token)
        .get(invoiceServicePath())
        .getBody()
        .as(Account[].class);
  }

  BigDecimal getRandomBalance() {
    return BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
  }

  String getRandomName() {
    return UUID.randomUUID().toString();
  }

  String invoiceServicePath(long id) {
    return invoiceServicePath() + "/" + id;
  }

  private String invoiceServicePath() {
    return String.format(ACCOUNTS_SERVICE_PATH, port);
  }

  @PostConstruct
  public void before() throws Exception {
    final User defaultUser = userMarian();

    given()
        .contentType(ContentType.JSON)
        .body(defaultUser)
        .post(usersServicePath() + "/register");
    token = authenticateUserAndGetToken(defaultUser);

    for (int i = 0; i < 10; ++i) {

      Account account = getAccount();

      accounts.add(account);
    }
  }

  Account getAccount() {
    AccountRequest accountRequest = AccountRequest.builder()
        .name(UUID.randomUUID().toString())
        .balance(getRandomBalance())
        .build();

    String response = given()
        .contentType(ContentType.JSON)
        .header("Authorization", token)
        .body(accountRequest)
        .when()
        .post(invoiceServicePath())
        .getBody()
        .asString();

    Long accountId = Long.parseLong(response);
    return Account.builder()
        .id(accountId)
        .name(accountRequest.getName())
        .balance(accountRequest.getBalance())
        .build();
  }

  @After
  public void afterCheck() {
    accounts.sort((first, second) -> (int) (first.getId() - second.getId()));

    Account[] accountsFromService = getAccounts();
    assertThat(accountsFromService.length, is(accounts.size()));

    int index = 0;
    for (Account account : accountsFromService) {
      collector.checkThat(account, is(equalTo(accounts.get(index++))));
    }

    for (Account account : accountsFromService) {
      given()
          .when()
          .header("Authorization", token)
          .delete(invoiceServicePath(account.getId()));
    }

    assertThat(getAccounts().length, is(0));
  }

  private UserDetails jsonToAuthResponse(String jsonAuthResponse) throws Exception {
    return mapper.readValue(jsonAuthResponse, UserDetails.class);
  }

  private String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  private String authenticateUserAndGetToken(User user) throws Exception {
    String response = given()
        .contentType(ContentType.JSON)
        .body(json(user))
        .post(usersServicePath() + "/authenticate")
        .getBody()
        .print();

    return jsonToAuthResponse(response).getToken();
  }

  AccountRequest convertAccountToAccountRequest(Account account) {
    return AccountRequest.builder()
        .name(account.getName())
        .balance(account.getBalance())
        .build();
  }

  private String usersServicePath() {
    return String.format(USERS_SERVICE_PATH, port);
  }

}
