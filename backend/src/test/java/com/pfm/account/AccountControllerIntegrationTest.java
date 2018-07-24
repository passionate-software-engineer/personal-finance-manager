package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ADAM_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JACEK_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JUREK_BALANCE_10_99;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_LUKASZ_BALANCE_1124;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MATEUSZ_BALANCE_200;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_PIOTR_BALANCE_9;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_SEBASTIAN_BALANCE_1_000_000;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
import com.pfm.Messages;
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
public class AccountControllerIntegrationTest {

  // TODO write cleaner, in most tests should get id of object returned from server then use
  // TODO - apply suggestions from CategoryControllerIntegrationTest

  private static final String INVOICES_SERVICE_PATH = "/accounts";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final long NOT_EXISTING_ID = 0;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Flyway flyway;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  @Test
  public void shouldAddAccount() throws Exception {
    this.mockMvc.perform(post(INVOICES_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(json(ACCOUNT_JACEK_BALANCE_1000)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByEmptyNameAndEmptyBalanceFields() throws Exception {
    Account accountWithoutName = new Account(null, null, null);

    this.mockMvc.perform(post(INVOICES_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(json(accountWithoutName)))
        .andExpect(
            content().string("[\"" + Messages.EMPTY_ACCOUNT_NAME + "\",\""
                + Messages.EMPTY_ACCOUNT_BALANCE + "\"]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldGetAccountById() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_LUKASZ_BALANCE_1124);

    this.mockMvc
        .perform(get(INVOICES_SERVICE_PATH + "/1")) // TODO you cannot assume the id will be 1
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingId() throws Exception {
    this.mockMvc
        .perform(get(INVOICES_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());

  }

  @Test
  public void shouldGetAllAccounts() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_SEBASTIAN_BALANCE_1_000_000);
    callRestServiceToAddAccount(ACCOUNT_PIOTR_BALANCE_9);

    this.mockMvc
        .perform(get(INVOICES_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$",
            hasSize(2))); // TODO you should assert entire response not just check if it returned 2
  }

  @Test
  public void shouldUpdateAccount() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_ADAM_BALANCE_0);

    this.mockMvc.perform(put(INVOICES_SERVICE_PATH + "/1")
        .contentType(CONTENT_TYPE)
        .content(json(ACCOUNT_MATEUSZ_BALANCE_200)))
        .andDo(print())
        .andExpect(status().isOk());

    this.mockMvc.perform(get(INVOICES_SERVICE_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Mateusz mBank saving account")))
        .andExpect(jsonPath("$.balance", is(equalTo("200.00"))));
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInUpdateMethod() throws Exception {
    Account accountToUpdate = ACCOUNT_ADAM_BALANCE_0;

    this.mockMvc
        .perform(put(INVOICES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .contentType(CONTENT_TYPE)
            .content(json(accountToUpdate)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCauseByNotValidAccountUpdateMethod() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_ADAM_BALANCE_0);
    Account accountToUpdate = Account.builder()
        .name("")
        .balance(ACCOUNT_ADAM_BALANCE_0.getBalance())
        .build();

    this.mockMvc
        .perform(put(INVOICES_SERVICE_PATH + "/1")
            .contentType(CONTENT_TYPE)
            .content(json(accountToUpdate)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldDeleteAccount() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_JUREK_BALANCE_10_99);

    this.mockMvc
        .perform(delete(INVOICES_SERVICE_PATH + "/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInDeleteMethod() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_JUREK_BALANCE_10_99);

    this.mockMvc
        .perform(delete(INVOICES_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private String json(Account account) throws Exception {
    return objectMapper.writeValueAsString(account);
  }

  private void callRestServiceToAddAccount(Account account) throws Exception {
    this.mockMvc.perform(post(INVOICES_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(json(account)))
        .andExpect(status().isOk());
  }
}