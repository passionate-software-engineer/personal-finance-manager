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
public class SimultaneouslyDeleteAccountTest {

    private static final String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
    private static final int THREAD_COUNT = 24; //set how much threads you want to start
    private ObjectMapper objectMapper = new ObjectMapper();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    public ErrorCollector collector = new ErrorCollector();

    @LocalServerPort
    private int port;

    private String servicePath() {
        return String.format(INVOICES_SERVICE_PATH, port);
    }

    @Test
    @ThreadCount(THREAD_COUNT)
    public void shouldDeleteSimultaneouslyAccountTest() throws Exception {
        BigDecimal balance = BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
        Account tempAccount = Account.builder()
                .name(UUID.randomUUID().toString())
                .balance(balance)
                .build();

        Long tempAccountId = Long.parseLong(given().contentType("application/json").body(tempAccount).when().post(servicePath()).getBody().asString());
        tempAccount.setId(tempAccountId);
        collector.checkThat(given().when().delete(servicePath() + "/" + tempAccount.getId().toString()).statusCode(), equalTo(200));
    }

    @After
    public void afterCheck() throws Exception {
        List<Account> accounts = getAccounts();
        collector.checkThat(accounts.size(), equalTo(0));
    }

    private List<Account> getAccounts() throws Exception {
        String json = given().when().get(servicePath()).getBody().asString();
        return objectMapper.readValue(json, new TypeReference<List<Account>>() {
        });
    }
}