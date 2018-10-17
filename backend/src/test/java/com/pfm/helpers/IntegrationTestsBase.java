package com.pfm.helpers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.auth.AppUser;
import com.pfm.auth.UserDetails;
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
  protected static final String USERS_SERVICE_PATH = "/users";
  protected static final String FILTERS_SERVICE_PATH = "/filters";
  protected static final String EXPORT_SERVICE_PATH = "/export";
  protected static final String IMPORT_SERVICE_PATH = "/import";

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
  protected long callRestServiceToAddAccountAndReturnId(Account account, String token) throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .header("Authorization", token)
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

  protected BigDecimal callRestServiceAndReturnAccountBalance(long accountId, String token) throws Exception {
    String response =
        mockMvc
            .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return jsonToAccount(response).getBalance();
  }

  protected List<Account> callRestToGetAllAccounts(String token) throws Exception {
    String response = mockMvc.perform(get(ACCOUNTS_SERVICE_PATH)
        .header("Authorization", token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getAccountsFromResponse(response);
  }

  private List<Account> getAccountsFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Account.class));
  }

  private Account jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Account.class);
  }

  //category
  protected long callRestToAddCategoryAndReturnId(Category category, String token) throws Exception {
    CategoryRequest categoryRequest = categoryToCategoryRequest(category);
    return addCategoryRequestAndReturnId(categoryRequest, token);
  }

  protected long callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(Category category, long parentCategoryId, String token)
      throws Exception {
    CategoryRequest categoryRequest = categoryToCategoryRequest(category);
    categoryRequest.setParentCategoryId(parentCategoryId);
    return addCategoryRequestAndReturnId(categoryRequest, token);
  }

  private long addCategoryRequestAndReturnId(CategoryRequest categoryRequest, String token) throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .header("Authorization", token)
                .content(json(categoryRequest))
                .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected Category callRestToGetCategoryById(long id, String token) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id)
        .header("Authorization", token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  protected void callRestToUpdateCategory(long id, CategoryRequest categoryRequest, String token) throws Exception {
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
            .header("Authorization", token)
            .content(json(categoryRequest))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isOk());
  }

  protected List<Category> callRestToGetAllCategories(String token) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH)
        .header("Authorization", token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  protected void callRestToDeleteCategoryById(long id, String token) throws Exception {
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + id)
        .header("Authorization", token))
        .andExpect(status().isOk());
  }

  private Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  private List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  protected Category convertCategoryRequestToCategoryAndSetId(long categoryId, long userId, CategoryRequest categoryRequest) {
    return Category.builder()
        .id(categoryId)
        .name(categoryRequest.getName())
        .parentCategory(categoryRequest.getParentCategoryId() == null ? null
            : categoryService.getCategoryByIdAndUserId(categoryRequest.getParentCategoryId(), userId)
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
  private long callRestToAddTransactionAndReturnId(TransactionRequest transactionRequest, long accountId, long categoryId, String token)
      throws Exception {
    transactionRequest.setCategoryId(categoryId);
    transactionRequest.getAccountPriceEntries().get(0).setAccountId(accountId);
    String response =
        mockMvc
            .perform(post(TRANSACTIONS_SERVICE_PATH)
                .header("Authorization", token)
                .content(json(transactionRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long callRestToAddTransactionAndReturnId(Transaction transaction, long accountId, long categoryId, String token) throws Exception {
    TransactionRequest transactionRequest = convertTransactionToTransactionRequest(transaction);
    return callRestToAddTransactionAndReturnId(transactionRequest, accountId, categoryId, token);
  }

  protected TransactionRequest convertTransactionToTransactionRequest(Transaction transaction) {
    return TransactionRequest.builder()
        .description(transaction.getDescription())
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .date(transaction.getDate())
        .categoryId(transaction.getCategoryId())
        .build();
  }

  protected Transaction setTransactionIdAccountIdCategoryId(Transaction transaction, long transactionId, long accountId, long categoryId) {
    transaction.setId(transactionId);
    transaction.setCategoryId(categoryId);
    transaction.getAccountPriceEntries().get(0).setAccountId(accountId);
    return transaction;
  }

  protected Transaction convertTransactionRequestToTransactionAndSetId(long transactionId, TransactionRequest transactionRequest) {
    return Transaction.builder()
        .id(transactionId)
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .categoryId(transactionRequest.getCategoryId())
        .description(transactionRequest.getDescription())
        .date(transactionRequest.getDate())
        .build();
  }

  protected Transaction callRestToGetTransactionById(long id, String token) throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + id)
        .header("Authorization", token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToTransaction(response);
  }

  protected void deleteTransactionById(long id, String token) throws Exception {
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + id)
        .header("Authorization", token))
        .andExpect(status().isOk());
  }

  protected List<Transaction> callRestToGetAllTransactionsFromDatabase(String token) throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH)
        .header("Authorization", token))
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
  protected long callRestServiceToAddFilterAndReturnId(FilterRequest filterRequest, String token) throws Exception {
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .header("Authorization", token)
                .content(json(filterRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long callRestServiceToAddFilterAndReturnId(Filter filter, String token) throws Exception {
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .header("Authorization", token)
                .content(json(convertFilterToFilterRequest(filter)))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected void callRestServiceToUpdateFilter(long id, FilterRequest filterRequest, String token) throws Exception {
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + id)
            .header("Authorization", token)
            .content(json(filterRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  protected void callRestToDeleteFilterById(long id, String token) throws Exception {
    mockMvc.perform(delete(FILTERS_SERVICE_PATH + "/" + id)
        .header("Authorization", token))
        .andExpect(status().isOk());
  }

  protected FilterRequest convertFilterToFilterRequest(Filter filter) {
    return FilterRequest.builder()
        .name(filter.getName())
        .categoryIds(filter.getCategoryIds())
        .accountIds(filter.getAccountIds())
        .description(filter.getDescription())
        .dateFrom(filter.getDateFrom())
        .dateTo(filter.getDateTo())
        .priceFrom(filter.getPriceFrom())
        .priceTo(filter.getPriceTo())
        .build();
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

  protected Filter getFilterById(long id, String token) throws Exception {
    String response = mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + id)
            .header("Authorization", token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToFilter(response);
  }

  protected List<Filter> callRestToGetAllFilters(String token) throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH)
        .header("Authorization", token))
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

  //users

  public long callRestToRegisterUserAndReturnUserId(AppUser appUser) throws Exception {
    String response =
        mockMvc
            .perform(post(USERS_SERVICE_PATH + "/register")
                .content(json(appUser))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  public String callRestToAuthenticateUserAndReturnToken(AppUser appUser) throws Exception {
    String response = mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(appUser)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    return jsonToAuthResponse(response).getToken();
  }

  public String callRestToRegisterAndAuthenticateUserAndReturnUserToken(AppUser appUser) throws Exception {
    callRestToRegisterUserAndReturnUserId(appUser);
    return callRestToAuthenticateUserAndReturnToken(appUser);
  }

  private UserDetails jsonToAuthResponse(String jsonAuthResponse) throws Exception {
    return mapper.readValue(jsonAuthResponse, UserDetails.class);
  }

}
