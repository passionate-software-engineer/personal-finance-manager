package com.pfm.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAccountById() {
        String accountJson = "{\"id\":1,\"name\":\"Piotrek\",\"balance\":1000}";

        this.mockMvc
                .perform(get("/accounts/1")
                .contentType("application/json;charset=UTF-8"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

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