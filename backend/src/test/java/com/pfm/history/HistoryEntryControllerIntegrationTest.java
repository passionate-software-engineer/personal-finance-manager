package com.pfm.history;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.AccountRequest;
import com.pfm.category.CategoryRequest;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.TransactionRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

// TODO rewrite test to use common helper methods and objects
public class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String PATH = "/history";
  private static final String ACCOUNTS_PATH = "/accounts";
  private static final String CATEGORY_PATH = "/categories";
  private static final String TRANSACTION_PATH = "/transactions";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  @Before
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldGetAllHistoryEntries() throws Exception {

    mockMvc.perform(post(ACCOUNTS_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(AccountRequest.builder().name("Bla").balance(BigDecimal.valueOf(1000)).build())))
        .andExpect(status().isOk());

    mockMvc.perform(post(ACCOUNTS_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(AccountRequest.builder().name("BlaBla").balance(BigDecimal.valueOf(2000)).build())))
        .andExpect(status().isOk());

    mockMvc.perform(post(CATEGORY_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Jedzenie").build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(ACCOUNTS_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(AccountRequest.builder().name("Konto 1000").balance(BigDecimal.valueOf(1000)).build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(ACCOUNTS_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(AccountRequest.builder().name("Konto 1000").balance(BigDecimal.valueOf(1000)).build())))
        .andExpect(status().isOk());

    mockMvc.perform(post(TRANSACTION_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(
            TransactionRequest.builder()
                .accountPriceEntries(Collections.singletonList(
                    AccountPriceEntry.builder()
                        .accountId(1L)
                        .price(BigDecimal.valueOf(100))
                        .build())
                )
                .categoryId(1L)
                .date(LocalDate.of(2018, 7, 2))
                .description("Chleb")
                .build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(AccountRequest.builder().name("Samochód").build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(TRANSACTION_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(
            TransactionRequest.builder()
                .accountPriceEntries(Arrays.asList(
                    AccountPriceEntry.builder()
                        .accountId(1L)
                        .price(BigDecimal.valueOf(100))
                        .build(),
                    AccountPriceEntry.builder()
                        .accountId(2L)
                        .price(BigDecimal.valueOf(100))
                        .build()
                    )
                )
                .categoryId(1L)
                .date(LocalDate.of(2018, 7, 2))
                .description("Chleb")
                .build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Samochód").build())))
        .andExpect(status().isOk());

    for (int i = 0; i < 2; ++i) {
      mockMvc.perform(put(TRANSACTION_PATH + "/1")
          .header(HttpHeaders.AUTHORIZATION, token)
          .contentType(JSON_CONTENT_TYPE)
          .content(mapper.writeValueAsString(
              TransactionRequest.builder()
                  .accountPriceEntries(Collections.singletonList(
                      AccountPriceEntry.builder()
                          .accountId(2L)
                          .price(BigDecimal.valueOf(10))
                          .build())
                  )
                  .categoryId(1L)
                  .date(LocalDate.of(2018, 6, 2))
                  .description("Chlebik")
                  .build())))
          .andExpect(status().isOk());
    }

    mockMvc.perform(delete(TRANSACTION_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());

    mockMvc.perform(delete(ACCOUNTS_PATH + "/2")
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());

    mockMvc.perform(post(CATEGORY_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Picie").build())))
        .andExpect(status().isOk());

    mockMvc.perform(post(CATEGORY_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Galaretka").parentCategoryId(1L).build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/3")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Galaretka").parentCategoryId(2L).build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/3")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Galaretka").build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/3")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Nozki w galarecie").build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/3")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Nozki w galaretce").parentCategoryId(2L).build())))
        .andExpect(status().isOk());

    mockMvc.perform(put(CATEGORY_PATH + "/3")
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(mapper.writeValueAsString(CategoryRequest.builder().name("Galaretka").parentCategoryId(2L).build())))
        .andExpect(status().isOk());

    mockMvc.perform(delete(CATEGORY_PATH + "/1")
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get(PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
        )
        .andExpect(content()
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(38)))
        .andExpect(jsonPath("$[0].entry", is(equalTo("Added Account 'Bla'"))))
        .andExpect(jsonPath("$[1].entry", is(equalTo("The value of 'Bla Account ' ''balance'' property is '1000.00'"))))
        .andExpect(jsonPath("$[2].entry", is(equalTo("Added Account 'BlaBla'"))))
        .andExpect(jsonPath("$[3].entry", is(equalTo("The value of 'BlaBla Account ' ''balance'' property is '2000.00'"))))
        .andExpect(jsonPath("$[4].entry", is(equalTo("Added Category 'Jedzenie'"))))
        .andExpect(jsonPath("$[5].entry", is(equalTo("Account name changed from 'Bla' to 'Konto 1000'"))))
        .andExpect(jsonPath("$[6].entry", is(equalTo("Account was updated but there were no changes"))))
        .andExpect(jsonPath("$[7].entry", is(equalTo("Added Transaction 'Chleb'"))))
        .andExpect(jsonPath("$[8].entry", is(equalTo("The value of 'Chleb Transaction' ''date'' property is '2018-07-02'"))))
        .andExpect(jsonPath("$[9].entry", is(equalTo("The value of 'Chleb Transaction' ''category'' property is '1'"))))
        .andExpect(jsonPath("$[10].entry", is(equalTo("The value of 'Chleb Transaction' ''price'' property is '100.00'"))))
        // TODO history should use descriptive names not ids of the objects
        // TODO make descriptions more human friendly
        .andExpect(jsonPath("$[11].entry", is(equalTo("The value of 'Chleb Transaction' ''account'' property is '1'"))))
        .andExpect(jsonPath("$[12].entry", is(equalTo("Category name changed from 'Jedzenie' to 'Samochód'"))))
        .andExpect(jsonPath("$[13].entry", is(equalTo("New account price entry was added to transaction. Account: 2, price: 100.00"))))
        .andExpect(jsonPath("$[14].entry", is(equalTo("Category was updated but there were no changes"))))
        .andExpect(jsonPath("$[15].entry", is(equalTo("Transaction description changed from 'Chleb' to 'Chlebik'"))))
        .andExpect(jsonPath("$[16].entry", is(equalTo("Transaction account changed from '1' to '2'"))))
        .andExpect(jsonPath("$[17].entry", is(equalTo("Transaction price changed from '100.00' to '10.00'"))))
        .andExpect(jsonPath("$[18].entry", is(equalTo("Account price entry was deleted from transaction. Account: 2, price: 100.00"))))
        .andExpect(jsonPath("$[19].entry", is(equalTo("Transaction date changed from '2018-07-02' to '2018-06-02'"))))
        .andExpect(jsonPath("$[20].entry", is(equalTo("Transaction was updated but there were no changes"))))
        .andExpect(jsonPath("$[21].entry", is(equalTo("The value of 'Chlebik Transaction' ''date'' property is '2018-06-02'"))))
        .andExpect(jsonPath("$[22].entry", is(equalTo("The value of 'Chlebik Transaction' ''category'' property is '1'"))))
        .andExpect(jsonPath("$[23].entry", is(equalTo("The value of 'Chlebik Transaction' ''price'' property is '10.00'"))))
        .andExpect(jsonPath("$[24].entry", is(equalTo("The value of 'Chlebik Transaction' ''account'' property is '2'"))))
        .andExpect(jsonPath("$[25].entry", is(equalTo("Deleted Transaction 'Chlebik'"))))
        .andExpect(jsonPath("$[26].entry", is(equalTo("The value of 'BlaBla Account ' ''balance'' property is '2000.00'"))))
        .andExpect(jsonPath("$[27].entry", is(equalTo("Deleted Account 'BlaBla'"))))
        .andExpect(jsonPath("$[28].entry", is(equalTo("Added Category 'Picie'"))))
        .andExpect(jsonPath("$[29].entry", is(equalTo("Added Category 'Galaretka'"))))
        .andExpect(jsonPath("$[30].entry", is(equalTo("The value of 'Galaretka Category ' ''parent'' property is 'Samochód'"))))
        .andExpect(jsonPath("$[31].entry", is(equalTo("Parent category of Galaretka category  changed from 'Samochód' to 'Picie'"))))
        .andExpect(jsonPath("$[32].entry", is(equalTo("Parent category of Galaretka category  changed from 'Picie' to ''Main Category''"))))
        .andExpect(jsonPath("$[33].entry", is(equalTo("Category name changed from 'Galaretka' to 'Nozki w galarecie'"))))
        .andExpect(jsonPath("$[34].entry", is(equalTo("Category name changed from 'Nozki w galarecie' to 'Nozki w galaretce'"))))
        .andExpect(jsonPath("$[35].entry", is(equalTo("Parent category of Nozki w galarecie category  changed from ''Main Category'' to 'Picie'"))))
        .andExpect(jsonPath("$[36].entry", is(equalTo("Category name changed from 'Nozki w galaretce' to 'Galaretka'"))))
        .andExpect(jsonPath("$[37].entry", is(equalTo("Deleted Category 'Samochód'"))))
        .andDo(print());
  }

}