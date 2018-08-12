package com.pfm.transaction;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JACEK_BALANCE_1000;
import static com.pfm.helpers.TestCategoryProvider.CATEGORY_CAR_NO_PARENT_CATEGORY;
import static com.pfm.helpers.TestCategoryProvider.CATEGORY_FOOD_NO_PARENT_CATEGORY;
import static com.pfm.helpers.TestTransactionProvider.getCarTransactionRequestWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.getFoodTransactionRequestWithNoAccountAndNoCategory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.category.Category;
import java.util.List;
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

  private static final String ACCOUNTS_SERVICE_PATH = "/accounts";
  private static final String TRANSACTIONS_SERVICE_PATH = "/transactions";
  private static final String CATEGORIES_SERVICE_PATH = "/categories";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final long NOT_EXISTING_ID = 0;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private Flyway flyway;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  @Test
  public void shouldAddTransaction() throws Exception {
    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(ACCOUNT_JACEK_BALANCE_1000);
    long foodCategoryId = callRestServiceToAddCategoryAndReturnId(
        CATEGORY_FOOD_NO_PARENT_CATEGORY);
    TransactionRequest transactionToAdd = getFoodTransactionRequestWithNoAccountAndNoCategory();
    //when
    long transactionId = callRestServiceToAddTransactionAndReturnId(transactionToAdd, jacekAccountId,
        foodCategoryId);
    //then
    assertThat(getTransactionById(transactionId),
        is(equalTo(convertTransactionRequestToTransactionAndSetId(transactionId,
            transactionToAdd))));
  }

  @Test
  public void shouldDeleteTransaction() throws Exception {
    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(ACCOUNT_JACEK_BALANCE_1000);
    long foodCategoryId = callRestServiceToAddCategoryAndReturnId(
        CATEGORY_FOOD_NO_PARENT_CATEGORY);
    TransactionRequest transactionToAdd = getFoodTransactionRequestWithNoAccountAndNoCategory();
    long transactionId = callRestServiceToAddTransactionAndReturnId(transactionToAdd, jacekAccountId, foodCategoryId);
    Transaction addedTransaction = convertTransactionRequestToTransactionAndSetId(transactionId,
        transactionToAdd);
    //when
    deleteTransactionById(transactionId);
    //then
    List<Transaction> allTransactionsInDb = getAllTransactionsFromDatabase();
    assertThat(allTransactionsInDb.size(), is(0));
    assertThat(allTransactionsInDb.contains(addedTransaction), is(false));
  }

  @Test
  public void shouldGetTransactions() throws Exception {
    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(ACCOUNT_JACEK_BALANCE_1000);
    long foodCategoryId = callRestServiceToAddCategoryAndReturnId(
        CATEGORY_FOOD_NO_PARENT_CATEGORY);
    long carCategoryId = callRestServiceToAddCategoryAndReturnId(
        CATEGORY_CAR_NO_PARENT_CATEGORY);
    TransactionRequest foodTransactionRequest = getFoodTransactionRequestWithNoAccountAndNoCategory();
    TransactionRequest carTransactionRequest = getCarTransactionRequestWithNoAccountAndNoCategory();
    long foodTransactionId = callRestServiceToAddTransactionAndReturnId(foodTransactionRequest, jacekAccountId, foodCategoryId);
    long carTransactionId = callRestServiceToAddTransactionAndReturnId(carTransactionRequest, jacekAccountId, carCategoryId);
    //when
    List<Transaction> allTransactionsInDb = getAllTransactionsFromDatabase();
    //then
    assertThat(allTransactionsInDb.size(), is(2));
    assertThat(allTransactionsInDb.contains(convertTransactionRequestToTransactionAndSetId(foodTransactionId, foodTransactionRequest)), is(true));
    assertThat(allTransactionsInDb.contains(convertTransactionRequestToTransactionAndSetId(carTransactionId, carTransactionRequest)), is(true));

  }

  private long callRestServiceToAddAccountAndReturnId(Account account) throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .content(json(account))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private long callRestServiceToAddTransactionAndReturnId(TransactionRequest transactionRequest, long accountId, long categoryId) throws Exception {
    transactionRequest.setCategoryId(categoryId);
    transactionRequest.setAccountId(accountId);
    String response =
        mockMvc
            .perform(post(TRANSACTIONS_SERVICE_PATH)
                .content(json(transactionRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private long callRestServiceToAddCategoryAndReturnId(Category category) throws Exception {
    String response =
        mockMvc
            .perform(post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }


  private Transaction convertTransactionRequestToTransactionAndSetId(long transactionId,
      TransactionRequest transactionRequest) {
    return Transaction.builder()
        .id(transactionId)
        .accountId(transactionRequest.getAccountId())
        .categoryId(transactionRequest.getCategoryId())
        .description(transactionRequest.getDescription())
        .date(transactionRequest.getDate())
        .price(transactionRequest.getPrice())
        .build();
  }

  private Transaction getTransactionById(long id) throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToTransaction(response);
  }

  private void deleteTransactionById(long id) throws Exception {
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  private List<Transaction> getAllTransactionsFromDatabase() throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  private String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  private Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  private Category jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Category.class);
  }

  private Transaction jsonToTransaction(String jsonTransaction) throws Exception {
    return mapper.readValue(jsonTransaction, Transaction.class);
  }

  private List<Transaction> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
  }
}