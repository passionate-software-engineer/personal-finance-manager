package com.pfm.AccountControllerTest;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext (classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class AccountControllerIntegrationTest{

    public List<String> readFile() throws IOException {
        FileReader fileReader = new FileReader("src\\test\\resources\\account.txt");
        List<String> accountList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String textLine = bufferedReader.readLine();
            do {
                accountList.add(textLine);
                textLine = bufferedReader.readLine();
            } while (textLine != null);
        }
        return accountList;
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldAddAccountTest() throws Exception {

        String accountJson = readFile().get(1);

            this.mockMvc.perform(post("/accounts/")
                .contentType("application/json;charset=UTF-8")
                .content(accountJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldGetAccountById() throws Exception {

       String accountJson = readFile().get(1);

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
        String accountJson = readFile().get(1);
        String accountJson2 = readFile().get(2);

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

        String accountJson = readFile().get(0);
        String accountJson2 = readFile().get(4);

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

        String accountJson = readFile().get(1);
        this.mockMvc.perform(post("/accounts/")
                .contentType(MediaType.valueOf("application/json;charset=UTF-8"))
                .content(accountJson))
                .andExpect(status().isCreated());

        this.mockMvc
                .perform(delete("/accounts/1"))
                .andExpect(status().isNoContent());
    }
}