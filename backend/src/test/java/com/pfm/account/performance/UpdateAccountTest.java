package com.pfm.account.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.account.Account;
import io.restassured.http.ContentType;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class UpdateAccountTest extends InvoicePerformanceTestBase {

  private final AtomicInteger counter = new AtomicInteger(0);

  @Test
  //  @ThreadCount(THREAD_COUNT) // TODO add wrapper running tests in multiple tests
  public void shouldUpdateSimultaneouslyMultipleAccounts() {

    Account account = accounts.get(counter.getAndIncrement());
    account.setBalance(getRandomBalance());
    account.setName(getRandomName());

    int statusCode = given()
        .contentType(ContentType.JSON)
        .body(convertAccountToAccountRequest(account))
        .header("Authorization", token)
        .when()
        .put(invoiceServicePath(account.getId()))
        .statusCode();

    assertThat(statusCode, is(200));
  }

}