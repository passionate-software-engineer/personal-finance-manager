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
import com.pfm.category.CategoryRequest;
import com.pfm.category.CategoryService;
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

  // TODO - MINOR - Try using multiple runners or Junit5 parametrized tests
  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  protected static final String ACCOUNTS_SERVICE_PATH = "/accounts";
  protected static final String CATEGORIES_SERVICE_PATH = "/categories";
  protected static final String TRANSACTIONS_SERVICE_PATH = "/transactions";
  protected static final String FILTERS_SERVICE_PATH = "/filters";

  protected static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  protected static final long NOT_EXISTING_ID = 0;

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected CategoryService categoryService;

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
                .content(json(convertAccountToAccountRequest(account)))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected AccountRequest convertAccountToAccountRequest(Account account) {
    return AccountRequest.builder()
        .name(account.getName())
        .balance(account.getBalance())
        .build();
  }

  protected BigDecimal callRestServiceAndReturnAccountBalance(long accountId) throws Exception {
    String response =
        mockMvc
            .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return jsonToAccount(response).getBalance();
  }

  private Account jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Account.class);
  }

  //category
  protected long callRestToAddCategoryAndReturnId(Category category) throws Exception {
    CategoryRequest categoryRequest = categoryToCategoryRequest(category);
    return addCategoryRequestAndReturnId(categoryRequest);
  }

  protected long callRestToAddCategoryAndReturnId(CategoryRequest categoryRequest) throws Exception {
    return addCategoryRequestAndReturnId(categoryRequest);
  }

  protected long callRestToaddCategoryWithSpecifiedParentCategoryIdAndReturnId(long parentCategoryId, Category category)
      throws Exception {
    CategoryRequest categoryRequest = categoryToCategoryRequest(category);
    categoryRequest.setParentCategoryId(parentCategoryId);
    return addCategoryRequestAndReturnId(categoryRequest);
  }

  private long addCategoryRequestAndReturnId(CategoryRequest categoryRequest) throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(categoryRequest))
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

  protected void callRestToUpdateCategory(long id, CategoryRequest categoryRequest) throws Exception {
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
            .content(json(categoryRequest))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isOk());
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

  private Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  private List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  protected Category convertCategoryRequestToCategoryAndSetId(long categoryId, CategoryRequest categoryRequest) {
    return Category.builder()
        .id(categoryId)
        .name(categoryRequest.getName())
        .parentCategory(categoryRequest.getParentCategoryId() == null ? null : categoryService.getCategoryById(categoryRequest.getParentCategoryId())
            .orElse(null))
        .build();
  }

  protected CategoryRequest categoryToCategoryRequest(Category category) {
    return CategoryRequest.builder()
        .name(category.getName())
        .parentCategoryId(category.getParentCategory() == null ? null : category.getParentCategory().getId())
        .build();
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

  private Transaction jsonToTransaction(String jsonTransaction) throws Exception {
    return mapper.readValue(jsonTransaction, Transaction.class);
  }

  private List<Transaction> getTransactionsFromResponse(String response) throws Exception {
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
