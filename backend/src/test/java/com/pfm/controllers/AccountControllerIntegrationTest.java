package com.pfm.controllers;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ADAM_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JACEK_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JUREK_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_LUKASZ_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MATEUSZ_BALANCE_200;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_PIOTREK_BALANCE_9;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_SEBASTIAN_BALANCE_1000;
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
import com.pfm.account.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerIntegrationTest {

  private static final String INVOICES_SERVICE_PATH = "/accounts";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldAddAccountTest() throws Exception {

    this.mockMvc.perform(post(INVOICES_SERVICE_PATH)
        .contentType(CONTENT_TYPE)
        .content(json(ACCOUNT_JACEK_BALANCE_1000)))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldGetAccountById() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_LUKASZ_BALANCE_1000);

    this.mockMvc
        .perform(get(INVOICES_SERVICE_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void shouldGetAllAccounts() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_SEBASTIAN_BALANCE_1000);
    callRestServiceToAddAccount(ACCOUNT_PIOTREK_BALANCE_9);

    this.mockMvc
        .perform(get(INVOICES_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldUpdateAccount() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_ADAM_BALANCE_1000);

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
  public void shouldDeleteAccount() throws Exception {
    callRestServiceToAddAccount(ACCOUNT_JUREK_BALANCE_1000);

    this.mockMvc
        .perform(delete(INVOICES_SERVICE_PATH + "/1"))
        .andExpect(status().isOk());
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