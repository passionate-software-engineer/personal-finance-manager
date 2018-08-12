package com.pfm.performance;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.After;
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
public class SimultaneouslyAddAccountTest {

  private static final String INVOICES_SERVICE_PATH = "http://localhost:%d/accounts";
  private final static int THREAD_COUNT = 24; //set how much threads you want to start

  private List<Account> addedAccounts = Collections.synchronizedList(new ArrayList<>());

  private ObjectMapper objectMapper = new ObjectMapper();
  private HttpHeaders headers = new HttpHeaders();
  private RestTemplate restTemplate = new RestTemplate();

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Before
  public void initialization() throws Exception {
    System.out.println("Before servicePath " + servicePath());
    headers.setContentType(MediaType.APPLICATION_JSON);
    //int temp = dataBaseCleaner();
  }

  @LocalServerPort
  private int port;

  private final AtomicLong counter = new AtomicLong(1L);

  private String servicePath() {
    return String.format(INVOICES_SERVICE_PATH, port);
  }

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldAddSimultaneouslyAccountTest() throws Exception {
    Account tempAccount = Account.builder()
            .name(UUID.randomUUID().toString())
            .balance(BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING))
            .build();

    HttpEntity<String> entity = new HttpEntity<String>((json(tempAccount)), headers);
    ResponseEntity<String> answer = restTemplate.exchange(servicePath(), HttpMethod.POST, entity, String.class);
    assertEquals(200, answer.getStatusCodeValue());

    tempAccount.setId(Long.parseLong(answer.getBody()));
    addedAccounts.add(tempAccount);
  }


  @After
  public void afterCheck() throws Exception {
    addedAccounts.sort((first, second) -> (int) (first.getId() - second.getId()));

    List<Account> accounts = getAccounts();
    assertThat(accounts.size(), is(THREAD_COUNT));

    int index = 0;
    for (Account account : accounts) {
      assertThat(account, is(addedAccounts.get(index++))); // TODO use error collector
    }
  }


  private String json(Account account) throws Exception {
    return objectMapper.writeValueAsString(account);
  }

  private List<Account> getAccounts() throws Exception {
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<String> answer = restTemplate.exchange(servicePath(), HttpMethod.GET, entity, String.class);
    return objectMapper.readValue(answer.getBody(), new TypeReference<List<Account>>() {
    });
  }

}