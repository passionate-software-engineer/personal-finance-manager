package com.pfm.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.pfm.account.Account;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimultaneouslyDeleteAccountTest extends SimultaneouslyTest {

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldDeleteSimultaneouslyAccountTest() throws Exception {
    BigDecimal balance = BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE))
        .setScale(2, RoundingMode.CEILING);
    Account account = Account.builder()
        .name(UUID.randomUUID().toString())
        .balance(balance)
        .build();
    String httpAnswer = given()
        .contentType("application/json")
        .body(account)
        .when()
        .post(servicePath())
        .getBody()
        .asString();
    Long accountId = Long.parseLong(httpAnswer);
    account.setId(accountId);
    collector.checkThat(
        given().when().delete(servicePath() + "/" + account.getId().toString()).statusCode(),
        equalTo(200));
  }

  @After
  public void afterCheck() throws Exception {
    List<Account> accounts = getAccounts();
    collector.checkThat(accounts.size(), equalTo(0));
  }
}