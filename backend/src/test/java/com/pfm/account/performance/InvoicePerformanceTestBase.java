package com.pfm.account.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.pfm.account.Account;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class InvoicePerformanceTestBase {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
  protected static final int THREAD_COUNT = 24;
  private static final String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();
  @Rule
  public final ErrorCollector collector = new ErrorCollector();
  protected List<Account> accounts = Collections.synchronizedList(new ArrayList<>());
  @LocalServerPort
  private int port;

  protected Account[] getAccounts() throws Exception {
    return given()
        .when()
        .get(invoiceServicePath())
        .getBody()
        .as(Account[].class);
  }

  protected BigDecimal getRandomBalance() {
    return BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
  }

  protected String getRandomName() {
    return UUID.randomUUID().toString();
  }

  protected String invoiceServicePath() {
    return String.format(INVOICES_SERVICE_PATH, port);
  }

  protected String invoiceServicePath(long id) {
    return invoiceServicePath() + "/" + id;
  }

  @PostConstruct
  public void before() {
    for (int i = 0; i < 10; ++i) {
      Account account = Account.builder()
          .name(getRandomName())
          .balance(getRandomBalance())
          .build();

      String response = given()
          .contentType(ContentType.JSON)
          .body(account)
          .when()
          .post(invoiceServicePath())
          .getBody()
          .asString();

      Long accountId = Long.parseLong(response);
      account.setId(accountId);

      accounts.add(account);
    }
  }

  @After
  public void afterCheck() throws Exception {
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
          .delete(invoiceServicePath(account.getId()));
    }

    assertThat(getAccounts().length, is(0));
  }
}
