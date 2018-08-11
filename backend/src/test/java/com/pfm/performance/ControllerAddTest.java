package com.pfm.performance;

import static org.junit.Assert.assertEquals;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.web.client.RestTemplate;

@RunWith(ConcurrentTestRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ControllerAddTest {

  private static final String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
  private final static int THREAD_COUNT = 8; //set how much threads you want to start

  private ObjectMapper objectMapper = new ObjectMapper();
  private HttpHeaders headers = new HttpHeaders();
  private RestTemplate restTemplate = new RestTemplate();

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Before
  public void initialization() throws Exception {
    System.out.println("Before servicePath "+servicePath());
    headers.setContentType(MediaType.APPLICATION_JSON);
    //int temp = dataBaseCleaner();
  }

  @LocalServerPort
  private int port;

  private String servicePath(){
    return String.format(INVOICES_SERVICE_PATH, port);
  }

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shoulAddAccountTest() throws Exception {
    System.out.println("Test servicePath "+servicePath());
    Account tempAccount = Account.builder()
        .id(666666L)
        .name(UUID.randomUUID().toString())
        .balance(BigDecimal.valueOf(1000))
        .build();
    HttpEntity<String> entity = new HttpEntity<String>((json(tempAccount)), headers);
    ResponseEntity<String> answer = restTemplate.exchange(servicePath(), HttpMethod.POST, entity, String.class);
    Assert.assertEquals(200, answer.getStatusCodeValue());
  }


  @After
  public void afterCheck() throws Exception{
    System.out.println("After servicePath "+servicePath());
    Assert.assertEquals(THREAD_COUNT, dataBaseCleaner());
  }

//  private Account toObject(String json) throws Exception {
//    return objectMapper.readValue(json, Account.class);
//  }

  private String json(Account account) throws Exception {
    return objectMapper.writeValueAsString(account);
  }

  private int dataBaseCleaner() throws Exception{
    System.out.println("DBCleaner servicePath "+servicePath());
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<String> answer = restTemplate.exchange(servicePath(), HttpMethod.GET, entity, String.class);
    TypeReference<List<Account>> mapType = new TypeReference<List<Account>>() {};
    List<Account> accountsReceived = objectMapper.readValue(answer.getBody(), mapType);
    for (Account account : accountsReceived) {
      System.out.println(servicePath()+"/"+account.getId().toString());
      answer = restTemplate.exchange(servicePath()+"/"+account.getId().toString(), HttpMethod.DELETE, entity, String.class);
    }
    return accountsReceived.size();
  }

}