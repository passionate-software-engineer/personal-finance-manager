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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimultaneouslyUpdateAccountTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    private static final String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
    private final static int THREAD_COUNT = 24; //set how much threads you want to start

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    private List<Account> addedAccounts = Collections.synchronizedList(new ArrayList<>());
    private ObjectMapper objectMapper = new ObjectMapper();
    public ErrorCollector collector = new ErrorCollector();

    @LocalServerPort
    private int port;
    private String servicePath() {
        return String.format(INVOICES_SERVICE_PATH, port);
    }

    @Test
    @ThreadCount(THREAD_COUNT)
    public void shouldUpdateSimultaneouslyAccountTest() throws Exception {
        Account tempAccount = Account.builder()
                .name(UUID.randomUUID().toString())
                .balance(BigDecimal.valueOf(1000))
                .build();

        Long tempAccountId = Long.parseLong(given().contentType("application/json").body(tempAccount).when().post(servicePath()).getBody().asString());
        tempAccount.setId(tempAccountId);

        BigDecimal balance = BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
        tempAccount.setBalance(balance);
        addedAccounts.add(tempAccount);
        collector.checkThat(given().contentType("application/json").body(tempAccount).when().put(servicePath() + "/" + tempAccount.getId().toString()).statusCode(), equalTo(200));
    }

    @After
    public void afterCheck() throws Exception {
        addedAccounts.sort((first, second) -> (int) (first.getId() - second.getId()));

        List<Account> accounts = getAccounts();
        collector.checkThat(accounts.size(), equalTo(THREAD_COUNT));

        int index = 0;
        for (Account account : accounts) {
            collector.checkThat(account, equalTo(addedAccounts.get(index++)));
            collector.checkThat(given().when().delete(servicePath() + "/" + account.getId().toString()).statusCode(), equalTo(200));
        }
    }

    private List<Account> getAccounts() throws Exception {
        String json = given().when().get(servicePath()).getBody().asString();
        return objectMapper.readValue(json, new TypeReference<List<Account>>() {
        });
    }
}