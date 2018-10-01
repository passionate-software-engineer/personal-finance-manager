//package com.pfm.account.performance;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.is;
//
//import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
//import com.pfm.account.Account;
//import io.restassured.http.ContentType;
//import java.util.concurrent.atomic.AtomicInteger;
//import org.junit.Test;
//
//public class UpdateAccountTest extends InvoicePerformanceTestBase {
//
//  private AtomicInteger counter = new AtomicInteger(0);
//
//  @Test
//  @ThreadCount(THREAD_COUNT)
//  public void shouldUpdateSimultaneouslyMultipleAccounts() {
//
//    Account account = accounts.get(counter.getAndIncrement());
//    account.setBalance(getRandomBalance());
//    account.setName(getRandomName());
//
//    int statusCode = given()
//        .contentType(ContentType.JSON)
//        .body(account)
//        .when()
//        .put(invoiceServicePath(account.getId()))
//        .statusCode();
//
//    collector.checkThat(statusCode, is(200));
//  }
//
//}