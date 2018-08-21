package com.pfm.performance;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimultaneouslyDeleteAccountTest extends SimultaneouslyTest{

    @Test
    @ThreadCount(THREAD_COUNT)
    public void shouldDeleteSimultaneouslyAccountTest() throws Exception {
        BigDecimal balance = BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
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
        collector.checkThat(given().when().delete(servicePath() + "/" + account.getId().toString()).statusCode(), equalTo(200));
    }

    @After
    public void afterCheck() throws Exception {
        List<Account> accounts = getAccounts();
        collector.checkThat(accounts.size(), equalTo(0));
    }
}