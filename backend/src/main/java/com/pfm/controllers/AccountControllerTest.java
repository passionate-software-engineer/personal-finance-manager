package com.pfm.controllers;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

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
    public void Test2getAccountById() throws Exception {

        this.mockMvc
                .perform((RequestBuilder) get("/accounts/1"))
                .andExpect((ResultMatcher) content().contentType("application/json;charset=UTF-8"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id", is(1)));
    }

    @org.junit.Test
    public void getAllAccounts() {
    }


    @org.junit.Test
    public void updateAccount() throws Exception {

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", is("200")));

    }


    @org.junit.Test
    public void Test3deleteAccount() throws Exception {

        String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":\"100\"}";
        this.mockMvc.perform(post("/accounts/")
                .contentType("application/json;charset=UTF-8")
                .content(accountJson))
                .andExpect(status().isCreated());

        this.mockMvc
                .perform(delete("/accounts/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(status().isOk());
    }
}