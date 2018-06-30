package com.pfm.AccountControllerTest;

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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @org.junit.Test
  public void Test1shouldAddAccountTest() throws Exception {

    String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":\"100\"}";

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andExpect(status().isCreated());
  }

  @Test
  public void Test2shouldGetAccountById() throws Exception {

    this.mockMvc
        .perform(get("/accounts/1"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @org.junit.Test
  public void Test3shouldGetAllAccounts() throws Exception {
    aaa(this.mockMvc);

  }

  public static void aaa(MockMvc mockMvc) throws Exception {
    String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":\"100\"}";
    String accountJson2 = "{\"id\":2,\"name\":\"Lukasz\",\"balance\":\"999\"}";

    mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson2))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/accounts/"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @org.junit.Test
  public void Test4shouldUpdateAccount() throws Exception {

    String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":\"100\"}";
    String accountJson2 = "{\"id\":1,\"name\":\"Jacek\",\"balance\":\"200\"}";

    this.mockMvc.perform(post("/accounts/")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson))
        .andDo(print()).andExpect(status().isCreated());

    this.mockMvc.perform(put("/accounts/1")
        .contentType("application/json;charset=UTF-8")
        .content(accountJson2))
        .andDo(print()).andExpect(status().isOk());

    this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/1"))
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Jacek")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance", is(200.00)));
  }

  @Test
  public void Test5shouldDeleteAccount() throws Exception {

    String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":\"100\"}";
    this.mockMvc.perform(post("/accounts/")
        .contentType(MediaType.valueOf("application/json;charset=UTF-8"))
        .content(accountJson))
        .andExpect(status().isCreated());

    this.mockMvc
        .perform(delete("/accounts/1"))
        .andExpect(status().isNoContent());
  }
}