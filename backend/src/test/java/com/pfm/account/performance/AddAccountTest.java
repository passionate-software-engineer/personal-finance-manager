package com.pfm.account.performance;

import static io.restassured.RestAssured.given;

import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.pfm.account.Account;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.Test;

public class AddAccountTest extends InvoicePerformanceTestBase {

  private volatile String token;

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldAddSimultaneouslyMultipleAccounts() throws Exception {

    token = authenticateUserAndGetToken(defaultAppUser);

    for (int i = 0; i < 10; ++i) {

      Account account = Account.builder()
          .name(UUID.randomUUID().toString())
          .balance(getRandomBalance())
          .build();

      String response = given()
          .contentType(ContentType.JSON)
          .header("Authorization", token)
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

}