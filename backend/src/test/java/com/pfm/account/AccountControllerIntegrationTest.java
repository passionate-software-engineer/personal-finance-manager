package com.pfm.account;

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
import com.pfm.helpers.JsonConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void shouldAddAccountTest() throws Exception {
    JsonConverter converter = new JsonConverter(objectMapper);
    Account account = Account.builder()
        .name("Jacek")
        .balance(BigDecimal.valueOf(1000)).build();
    String accountJson = converter.convertFromAccountToJson(account);

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andExpect(status().isCreated());
  }

  @Test
  public void shouldGetAccountById() throws Exception {
    JsonConverter converter = new JsonConverter(objectMapper);
    Account account = Account.builder()
        .name("Jacek")
        .balance(BigDecimal.valueOf(1000)).build();
    String accountJson = converter.convertFromAccountToJson(account);

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andExpect(status().isCreated());
    this.mockMvc
        .perform(get("/accounts/1"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void shouldGetAllAccounts() throws Exception {
    JsonConverter converter = new JsonConverter(objectMapper);
    Account account = Account.builder()
        .name("Jacek")
        .balance(BigDecimal.valueOf(1000)).build();
    Account account2 = Account.builder()
        .name("Piotrek")
        .balance(BigDecimal.valueOf(9)).build();
    String accountJson = converter.convertFromAccountToJson(account);
    String accountJson2 = converter.convertFromAccountToJson(account2);

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andExpect(status().isCreated());

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson2))
        .andExpect(status().isCreated());

    this.mockMvc
        .perform(get("/accounts/"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldUpdateAccount() throws Exception {
    JsonConverter converter = new JsonConverter(objectMapper);
    Account account = Account.builder()
        .name("Adam")
        .balance(BigDecimal.valueOf(1000)).build();
    Account account2 = Account.builder()
        .name("Jacek")
        .balance(BigDecimal.valueOf(200.00)).build();
    String accountJson = converter.convertFromAccountToJson(account);
    String accountJson2 = converter.convertFromAccountToJson(account2);

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andDo(print())
        .andExpect(status().isCreated());

    this.mockMvc.perform(put("/accounts/1")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson2))
        .andDo(print())
        .andExpect(status().isOk());

    this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/1"))
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Jacek")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance", is(200.00)));
  }

  @Test
  public void shouldDeleteAccount() throws Exception {
    JsonConverter converter = new JsonConverter(objectMapper);
    Account account = Account.builder()
        .name("Adam")
        .balance(BigDecimal.valueOf(1000)).build();
    String accountJson = converter.convertFromAccountToJson(account);

    this.mockMvc.perform(post("/accounts/")
        .contentType(MediaType.valueOf("application/json;charset=UTF-8"))
        .content(accountJson))
        .andExpect(status().isCreated());

    this.mockMvc
        .perform(delete("/accounts/1"))
        .andExpect(status().isOk());
  }
}