package com.pfm.performance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.List;

import static io.restassured.RestAssured.given;

public class SimultaneouslyTest {

    final static String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
    final static int THREAD_COUNT = 24; //set how much threads you want to start

    @LocalServerPort
    private int port;

    String servicePath() {
        return String.format(INVOICES_SERVICE_PATH, port);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    public ErrorCollector collector = new ErrorCollector();

    public List<Account> getAccounts() throws Exception {
        String json = given()
                .when()
                .get(servicePath())
                .getBody()
                .asString();
        return objectMapper.readValue(json, new TypeReference<List<Account>>() {
        });
    }
}
