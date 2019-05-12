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
  public void shouldUpdateSimultaneouslyMultipleAccounts() throws InterruptedException {
    runInMultipleThreads(() -> {

      Account account = accounts.get(counter.getAndIncrement());
      account.setBalance(getRandomBalance());
      account.setName(getRandomName());
      account.setCurrency(account.getCurrency());

      int statusCode = given()
          .contentType(ContentType.JSON)
          .body(convertAccountToAccountRequest(account))
          .header("Authorization", token)
          .when()
          .put(invoiceServicePath(account.getId()))
          .statusCode();

      // TODO - it has no effect - exception will be ignored by executor - need to add custom uncaught exception handler
      assertThat(statusCode, is(200));

    });
  }
}
