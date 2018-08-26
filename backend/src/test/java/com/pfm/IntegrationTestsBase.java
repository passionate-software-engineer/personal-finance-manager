package com.pfm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.category.Category;
import com.pfm.category.CategoryController.CategoryRequest;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterRequest;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import junitparams.JUnitParamsRunner;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestsBase {

  protected static final String ACCOUNTS_SERVICE_PATH = "/accounts";
  protected static final String CATEGORIES_SERVICE_PATH = "/categories";
  protected static final String TRANSACTIONS_SERVICE_PATH = "/transactions";
  protected static final String FILTERS_SERVICE_PATH = "/filters";

  protected static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  protected static final long NOT_EXISTING_ID = 0;

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected Flyway flyway;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  //all
  protected String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  //account
  protected long callRestServiceToAddAccountAndReturnId(Account account) throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .content(json(account))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long callRestServiceToAddAccountAndReturnId(AccountRequest accountRequest) throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .content(json(accountRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected BigDecimal callRestServiceAndReturnAccountBalance(long accountId) throws Exception {
    String response =
        mockMvc
            .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return jsonToAccount(response).getBalance();
  }

  protected Account jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Account.class);
  }


  //category
  protected long callRestToaddCategoryAndReturnId(CategoryRequest category) throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected Category callRestToGetCategoryById(long id) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  protected List<Category> callRestToGetAllCategories() throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  protected void callRestToDeleteCategoryById(long id) throws Exception {
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  protected Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  protected List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  //transaction
  protected long callRestServiceToAddTransactionAndReturnId(TransactionRequest transactionRequest, long accountId, long categoryId) throws Exception {
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

  protected long callRestServiceToAddCategoryAndReturnId(Category category) throws Exception {
    String response =
        mockMvc
            .perform(post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected Transaction convertTransactionRequestToTransactionAndSetId(long transactionId, TransactionRequest transactionRequest) {
    return Transaction.builder()
        .id(transactionId)
        .accountId(transactionRequest.getAccountId())
        .categoryId(transactionRequest.getCategoryId())
        .description(transactionRequest.getDescription())
        .date(transactionRequest.getDate())
        .price(transactionRequest.getPrice())
        .build();
  }

  protected Transaction callRestToGetTransactionById(long id) throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToTransaction(response);
  }

  protected void deleteTransactionById(long id) throws Exception {
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  protected List<Transaction> callRestToGetAllTransactionsFromDatabase() throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getTransactionsFromResponse(response);
  }

  protected Transaction jsonToTransaction(String jsonTransaction) throws Exception {
    return mapper.readValue(jsonTransaction, Transaction.class);
  }

  protected List<Transaction> getTransactionsFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
  }

  //filters
  protected long callRestServiceToAddFilterAndReturnId(FilterRequest filterRequest) throws Exception {
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .content(json(filterRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected void callRestServiceToUpdateFilter(long id, FilterRequest filterRequest) throws Exception {
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + id)
            .content(json(filterRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  protected void callRestToDeleteFilterById(long id) throws Exception {
    mockMvc.perform(delete(FILTERS_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  protected Filter convertFilterRequestToFilterAndSetId(long filterId, FilterRequest filterRequest) {
    if (filterRequest.getCategoryIds() == null) {
      filterRequest.setCategoryIds(new ArrayList<>());
    }

    if (filterRequest.getAccountIds() == null) {
      filterRequest.setAccountIds(new ArrayList<>());
    }
    return Filter.builder()
        .id(filterId)
        .name(filterRequest.getName())
        .dateFrom(filterRequest.getDateFrom())
        .dateTo(filterRequest.getDateTo())
        .accountIds(filterRequest.getAccountIds())
        .categoryIds(filterRequest.getCategoryIds())
        .priceFrom(filterRequest.getPriceFrom())
        .priceTo(filterRequest.getPriceTo())
        .description(filterRequest.getDescription())
        .build();
  }

  protected Filter getFilterById(long id) throws Exception {
    String response = mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToFilter(response);
  }

  protected List<Filter> callRestToGetAllFilters() throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getFiltersFromResponse(response);
  }

  protected void deleteFilterById(long id) throws Exception {
    mockMvc.perform(delete(FILTERS_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  protected List<Filter> getAllFiltersFromDatabase() throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getFiltersFromResponse(response);
  }

  private Filter jsonToFilter(String jsonFilter) throws Exception {
    return mapper.readValue(jsonFilter, Filter.class);
  }

  private List<Filter> getFiltersFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, Filter.class));
  }
}
