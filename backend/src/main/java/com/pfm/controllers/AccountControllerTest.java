package com.pfm.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAccountById() throws Exception {

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
    public void shouldAddAccountTest() {

    }

    @org.junit.Test
    public void updateAccount() {
    }

    @org.junit.Test
    public void deleteAccount() {
    }
}