package com.pfm.account.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.pfm.account.Account;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class DeleteAccountTest extends InvoicePerformanceTestBase {

  private final AtomicInteger counter = new AtomicInteger(0);

  @Test
  public void shouldDeleteSimultaneouslyMultipleAccounts() throws InterruptedException {
    runInMultipleThreads(() -> {

      Account account = accounts.get(counter.getAndAdd(2));
      accounts.remove(account);

      assertThat(
          given()
              .when()
              .header("Authorization", token)
              .delete(invoiceServicePath(account.getId()))
              .statusCode(),
          equalTo(200)
      );

    });
  }

}