package com.pfm.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.pfm.account.Account;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimultaneouslyUpdateAccountTest extends SimultaneouslyTest {

  private List<Account> addedAccounts = Collections.synchronizedList(new ArrayList<>());

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldUpdateSimultaneouslyAccountTest() throws Exception {
    Account account = Account.builder()
        .name(UUID.randomUUID().toString())
        .balance(BigDecimal.valueOf(1000))
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

    BigDecimal balance = BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE))
        .setScale(2, RoundingMode.CEILING);
    account.setBalance(balance);
    addedAccounts.add(account);
    collector.checkThat(given().contentType("application/json").body(account).when()
        .put(servicePath() + "/" + account.getId().toString()).statusCode(), equalTo(200));
  }

  @After
  public void afterCheck() throws Exception {
    addedAccounts.sort((first, second)
        -> (int) (first.getId() - second.getId()));

    List<Account> accounts = getAccounts();
    collector.checkThat(accounts.size(), equalTo(THREAD_COUNT));

    int index = 0;
    for (Account account : accounts) {
      collector.checkThat(account, is(equalTo(addedAccounts.get(index++))));
      collector.checkThat(
          given().when().delete(servicePath() + "/" + account.getId().toString()).statusCode(),
          equalTo(200));
    }
  }
}