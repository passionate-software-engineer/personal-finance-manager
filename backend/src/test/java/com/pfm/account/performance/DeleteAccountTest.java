package com.pfm.account.performance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.pfm.account.Account;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class DeleteAccountTest extends InvoicePerformanceTestBase {

  private final AtomicInteger counter = new AtomicInteger(0);

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldDeleteSimultaneouslyMultipleAccounts() {

    Account account = accounts.get(counter.getAndAdd(2));
    accounts.remove(account);

    collector.checkThat(
        given()
            .when()
            .header("Authorization", token)
            .delete(invoiceServicePath(account.getId()))
            .statusCode(),
        equalTo(200)
    );
  }

}