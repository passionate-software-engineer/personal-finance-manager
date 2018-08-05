package com.pfm.transaction;

import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_ACCOUNT;
import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_CATEGORY;

import static com.pfm.helpers.TestTransactionProvider.transactionRequest;
import static com.pfm.helpers.TestTransactionProvider.transactionRequest1;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.transaction.TransactionController.TransactionRequest;

import org.flywaydb.core.Flyway;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

  private static final String TRANSACTION_SERVICE_PATH = "/transactions";
  private static final String ACCOUNT_SERVICE_PATH = "/accounts";
  private static final String CATEGORY_SERVICE_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final long NOT_EXISTING_ID = 0;


  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private Flyway flyway;

  @Before
  public void before() throws Exception {
    flyway.clean();
    flyway.migrate();

  }

  @Test
  public void shouldAddTransaction() throws Exception {
    callRestServiceToAddAccount(MOCK_TRANSACTION_ACCOUNT);
    callRestServiceToAddCategory(MOCK_TRANSACTION_CATEGORY);

    this.mockMvc.perform(post(TRANSACTION_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(jsonTransaction(transactionRequest)))
        .andExpect(status().isOk());
  }

  @Test
  public void getTransactionById() throws Exception {
    callRestServiceToAddAccount(MOCK_TRANSACTION_ACCOUNT);
    callRestServiceToAddCategory(MOCK_TRANSACTION_CATEGORY);
    callRestServiceToAddTransaction(transactionRequest);

    this.mockMvc
        .perform(get(TRANSACTION_SERVICE_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void getTransactions() throws Exception {
    callRestServiceToAddAccount(MOCK_TRANSACTION_ACCOUNT);
    callRestServiceToAddCategory(MOCK_TRANSACTION_CATEGORY);
    callRestServiceToAddTransaction(transactionRequest);
    callRestServiceToAddTransaction(transactionRequest1);

    this.mockMvc
        .perform(get(TRANSACTION_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", is(1)))
        .andExpect(jsonPath("$.[0].description", is("Cinema")))
        .andExpect(jsonPath("$.[0].category.id", is(1)))
        .andExpect(jsonPath("$.[0].category.name", is("entertainment")))
        .andExpect(jsonPath("$.[0].account.id", is(1)))
        .andExpect(jsonPath("$.[0].account.name", is("ING BANK")))
        .andExpect(jsonPath("$.[0].account.balance", is(equalTo("1500.00"))))
        .andExpect(jsonPath("$.[0].price", is(equalTo("150.00"))))
        .andExpect(jsonPath("$.[1].id", is(2)))
        .andExpect(jsonPath("$.[1].description", is("Food")))
        .andExpect(jsonPath("$.[1].category.id", is(1)))
        .andExpect(jsonPath("$.[1].category.name", is("entertainment")))
        .andExpect(jsonPath("$.[1].account.id", is(1)))
        .andExpect(jsonPath("$.[1].account.name", is("ING BANK")))
        .andExpect(jsonPath("$.[1].account.balance", is(equalTo("1500.00"))))
        .andExpect(jsonPath("$.[1].price", is(equalTo("600.00"))));
  }

  @Test
  public void updateAccount() throws Exception {
    callRestServiceToAddAccount(MOCK_TRANSACTION_ACCOUNT);
    callRestServiceToAddCategory(MOCK_TRANSACTION_CATEGORY);
    callRestServiceToAddTransaction(transactionRequest);

    this.mockMvc.perform(put(TRANSACTION_SERVICE_PATH + "/1")
        .contentType(CONTENT_TYPE)
        .content(jsonTransaction(transactionRequest1)))
        .andDo(print())
        .andExpect(status().isOk());
    this.mockMvc
        .perform(get(TRANSACTION_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id", is(1)))
        .andExpect(jsonPath("$.[0].description", is("Food")))
        .andExpect(jsonPath("$.[0].category.id", is(1)))
        .andExpect(jsonPath("$.[0].category.name", is("entertainment")))
        .andExpect(jsonPath("$.[0].account.id", is(1)))
        .andExpect(jsonPath("$.[0].account.name", is("ING BANK")))
        .andExpect(jsonPath("$.[0].account.balance", is(equalTo("1500.00"))))
        .andExpect(jsonPath("$.[0].price", is(equalTo("600.00"))));
  }

  @Test
  public void deleteTransaction() throws Exception {
    callRestServiceToAddAccount(MOCK_TRANSACTION_ACCOUNT);
    callRestServiceToAddCategory(MOCK_TRANSACTION_CATEGORY);
    callRestServiceToAddTransaction(transactionRequest);

    this.mockMvc
        .perform(delete(TRANSACTION_SERVICE_PATH + "/1"))
        .andExpect(status().isOk());
  }

  private void callRestServiceToAddAccount(Account account) throws Exception {
    this.mockMvc.perform(post(ACCOUNT_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(jsonAccount(account)))
        .andExpect(status().isOk());
  }

  private void callRestServiceToAddCategory(Category category) throws Exception {
    this.mockMvc.perform(post(CATEGORY_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(jsonCategory(category)))
        .andExpect(status().isOk());
  }

  private void callRestServiceToAddTransaction(TransactionRequest transactionRequest)
      throws Exception {
    this.mockMvc.perform(post(TRANSACTION_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(jsonTransaction(transactionRequest)))
        .andExpect(status().isOk());
  }

  private String jsonTransaction(TransactionRequest transactionRequest) throws Exception {
    return mapper.writeValueAsString(transactionRequest);
  }

  private String jsonAccount(Account account) throws Exception {
    return mapper.writeValueAsString(account);
  }

  private String jsonCategory(Category category) throws Exception {
    return mapper.writeValueAsString(category);
  }
}