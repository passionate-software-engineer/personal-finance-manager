package com.pfm.helpers;

import static com.pfm.account.AccountControllerIntegrationTest.MARK_AS_ARCHIVED;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestTransactionProvider.foodPlannedTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRequest;
import com.pfm.account.type.AccountTypeService;
import com.pfm.auth.Token;
import com.pfm.auth.Tokens;
import com.pfm.auth.User;
import com.pfm.auth.UserDetails;
import com.pfm.auth.UserService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.category.requests.CategoryAddRequest;
import com.pfm.category.requests.CategoryRequestBase;
import com.pfm.category.requests.CategoryUpdateRequest;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.export.ExportResult;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterRequest;
import com.pfm.transaction.RecurrencePeriod;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionController.CommitResult;
import com.pfm.transaction.TransactionRequest;
import com.pfm.transaction.TransactionsHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestsBase {

  protected static final String ACCOUNTS_SERVICE_PATH = "/accounts";
  protected static final String CATEGORIES_SERVICE_PATH = "/categories";
  protected static final String TRANSACTIONS_SERVICE_PATH = "/transactions";
  protected static final String SET_AS_RECURRENT = "/setAsRecurrent";
  protected static final String USERS_SERVICE_PATH = "/users";
  protected static final String FILTERS_SERVICE_PATH = "/filters";
  protected static final String CURRENCIES_SERVICE_PATH = "/currencies";
  protected static final String ACCOUNT_TYPE_SERVICE_PATH = "/accountTypes"; // TODO - add tests for controller
  protected static final String EXPORT_SERVICE_PATH = "/export";
  protected static final String IMPORT_SERVICE_PATH = "/import";
  protected static final String COMMIT_OVERDUE = "/commitOverdue";

  protected static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
  protected static final long NOT_EXISTING_ID = 0;
  private static final String SET_AS_DEFAULT = "/setAsDefault";
  private static final String SET_AS_NOT_DEFAULT = "/setAsNotDefault";


  @Autowired
  protected UserService userService;

  @Autowired
  protected MockMvc mockMvc;

  @Qualifier("pfmObjectMapper")
  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected TransactionsHelper helper;

  @Autowired
  protected CategoryService categoryService;

  @Autowired
  protected CurrencyService currencyService;

  @Autowired
  protected AccountTypeService accountTypeService;

  @Autowired
  protected Flyway flyway;
  protected String token;
  protected long userId;

  @BeforeEach
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  protected String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  protected long callRestServiceToAddAccountAndReturnId(Account account, String token)
      throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(convertAccountToAccountRequest(account)))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected AccountRequest convertAccountToAccountRequest(Account account) {
    return AccountRequest.builder()
        .name(account.getName())
        .accountTypeId(account.getType().getId())
        .balance(account.getBalance())
        .currencyId(account.getCurrency().getId())
        .build();
  }

  protected AccountTypeRequest convertAccountTypeToAccountTypeRequest(AccountType accountType) {
    return AccountTypeRequest.builder()
        .name(accountType.getName())
        .build();
  }

  protected BigDecimal callRestServiceAndReturnAccountBalance(long accountId, String token)
      throws Exception {
    String response =
        mockMvc
            .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId)
                .header(HttpHeaders.AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return jsonToAccount(response).getBalance();
  }

  protected List<Account> callRestToGetAllAccounts(String token) throws Exception {
    String response = mockMvc.perform(get(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getAccountsFromResponse(response);
  }

  protected void callRestToDeleteAccountById(long id, String token) throws Exception {
    mockMvc.perform(delete(ACCOUNTS_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  protected void callRestToUpdateAccount(long id, AccountRequest accountRequest, String token)
      throws Exception {
    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + id)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(accountRequest))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isOk());
  }

  protected int callRestToMarkAccountAsArchived(long accountId) throws Exception {
    return mockMvc.perform(
        patch(ACCOUNTS_SERVICE_PATH + "/" + accountId + MARK_AS_ARCHIVED)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  protected ExportResult callRestToExportAllDataAndReturnExportResult(String token)
      throws Exception {
    String response =
        mockMvc
            .perform(get(EXPORT_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(JSON_CONTENT_TYPE)
            )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return mapper.readValue(response, ExportResult.class);
  }

  protected void callRestToImportAllData(String token, ExportResult dataToImport) throws Exception {
    mockMvc
        .perform(post(IMPORT_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(dataToImport))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isCreated());

  }

  private List<Account> getAccountsFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Account.class));
  }

  private Account jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Account.class);
  }

  private CommitResult jsonToCommitResult(String response) throws Exception {
    return mapper.readValue(response, CommitResult.class);
  }

  protected long callRestToAddCategoryAndReturnId(Category category, String token)
      throws Exception {
    CategoryAddRequest categoryRequest = convertCategoryToCategoryAddRequest(category);
    return addCategoryRequestAndReturnId(categoryRequest, token);
  }

  protected long callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(Category category,
      long parentCategoryId, String token)
      throws Exception {
    CategoryAddRequest categoryRequest = convertCategoryToCategoryAddRequest(category);
    categoryRequest.setParentCategoryId(parentCategoryId);
    return addCategoryRequestAndReturnId(categoryRequest, token);
  }

  private long addCategoryRequestAndReturnId(CategoryAddRequest categoryRequest, String token)
      throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(categoryRequest))
                .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected Category callRestToGetCategoryById(long id, String token) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  protected void callRestToUpdateCategory(long id, CategoryRequestBase categoryRequest, String token)
      throws Exception {
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(categoryRequest))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isOk());
  }

  protected List<Category> callRestToGetAllCategories(String token) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  protected void callRestToDeleteCategoryById(long id, String token) throws Exception {
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  private Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  private List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  protected <T extends CategoryRequestBase> Category convertCategoryRequestToCategoryAndSetId(long categoryId, long userId,
      T categoryRequest) {
    return Category.builder()
        .id(categoryId)
        .name(categoryRequest.getName())
        .parentCategory(categoryRequest.getParentCategoryId() == null ? null
            : categoryService
                .getCategoryByIdAndUserId(categoryRequest.getParentCategoryId(), userId)
                .orElse(null))
        .priority(categoryRequest.getPriority())
        .build();
  }

  protected CategoryUpdateRequest convertCategoryToCategoryUpdateRequest(Category category) {
    return CategoryUpdateRequest.builder()
        .name(category.getName())
        .parentCategoryId(
            category.getParentCategory() == null ? null : category.getParentCategory().getId())
        .priority(category.getPriority())
        .build();
  }

  protected CategoryAddRequest convertCategoryToCategoryAddRequest(Category category) {
    return CategoryAddRequest.builder()
        .name(category.getName())
        .parentCategoryId(
            category.getParentCategory() == null ? null : category.getParentCategory().getId())
        .priority(category.getPriority())
        .build();
  }

  private long callRestToAddTransactionAndReturnId(TransactionRequest transactionRequest,
      long accountId, long categoryId,
      String token)
      throws Exception {
    transactionRequest.setCategoryId(categoryId);
    transactionRequest.getAccountPriceEntries().get(0).setAccountId(accountId);
    String response =
        mockMvc
            .perform(post(TRANSACTIONS_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(transactionRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long callRestToAddTransactionAndReturnId(Transaction transaction, long accountId,
      long categoryId, String token)
      throws Exception {
    TransactionRequest transactionRequest = helper
        .convertTransactionToTransactionRequest(transaction);
    return callRestToAddTransactionAndReturnId(transactionRequest, accountId, categoryId, token);
  }

  protected long callRestToAddFirstTestTransactionAndReturnId() throws Exception {
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    return callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(),
        jacekAccountId, foodCategoryId, token);
  }

  protected long callRestToAddFirstTestPlannedTransactionAndReturnId() throws Exception {
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    return callRestToAddTransactionAndReturnId(foodPlannedTransactionWithNoAccountAndNoCategory(),
        jacekAccountId, foodCategoryId, token);
  }

  protected Transaction setTransactionIdAccountIdCategoryId(Transaction transaction,
      long transactionId,
      long accountId, long categoryId) {
    transaction.setId(transactionId);
    transaction.setCategoryId(categoryId);
    transaction.getAccountPriceEntries().get(0).setAccountId(accountId);
    return transaction;
  }

  protected Transaction convertTransactionRequestToTransactionAndSetId(long transactionId,
      TransactionRequest transactionRequest) {
    return Transaction.builder()
        .id(transactionId)
        .accountPriceEntries(transactionRequest.getAccountPriceEntries())
        .categoryId(transactionRequest.getCategoryId())
        .description(transactionRequest.getDescription())
        .date(transactionRequest.getDate())
        .isPlanned(transactionRequest.isPlanned())
        .recurrencePeriod(transactionRequest.getRecurrencePeriod())
        .build();
  }

  protected Transaction callRestToGetTransactionById(long id, String token) throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToTransaction(response);
  }

  protected long callRestToUpdateTransactionAndReturnId(long transactionId,
      TransactionRequest transactionRequest, String token)
      throws Exception {
    return Long.parseLong(mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + transactionId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getContentAsString());
  }

  protected CommitResult callRestToUpdateTransactionAndReturnCommitResult(long transactionId,
      TransactionRequest transactionRequest,
      String token)
      throws Exception {
    return jsonToCommitResult(mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + transactionId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionRequest)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getContentAsString());
  }

  protected void callRestToDeleteTransactionById(long id, String token) throws Exception {
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  protected void callRestToDeletePlannedTransactionById(long id, String token) throws Exception {
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
  }

  private Stream<Transaction> callRestToGetStreamOfAllPlannedAndNotPlannedTransactions(String token)
      throws Exception {
    String response = mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getTransactionsFromResponse(response).stream();
  }

  protected List<Transaction> callRestToGetAllTransactionsFromDatabase(String token)
      throws Exception {
    return callRestToGetStreamOfAllPlannedAndNotPlannedTransactions(token)
        .filter(transaction -> !transaction.isPlanned())
        .collect(Collectors.toList());
  }

  protected List<Transaction> callRestToGetAllPlannedTransactionsFromDatabase(String token)
      throws Exception {
    return callRestToGetStreamOfAllPlannedAndNotPlannedTransactions(token)
        .filter(Transaction::isPlanned)
        .collect(Collectors.toList());
  }

  private Transaction jsonToTransaction(String jsonTransaction) throws Exception {
    return mapper.readValue(jsonTransaction, Transaction.class);
  }

  private List<Transaction> getTransactionsFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
  }

  protected long callRestServiceToAddFilterAndReturnId(FilterRequest filterRequest, String token)
      throws Exception {
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(filterRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long callRestServiceToAddFilterAndReturnId(Filter filter, String token)
      throws Exception {
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(convertFilterToFilterRequest(filter)))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected void callRestServiceToUpdateFilter(long id, FilterRequest filterRequest, String token)
      throws Exception {
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + id)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(filterRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  protected void callRestToDeleteFilterById(long id, String token) throws Exception {
    mockMvc.perform(delete(FILTERS_SERVICE_PATH + "/" + id)
        .header(HttpHeaders.AUTHORIZATION, token))
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
        .isDefault(filter.isDefault())
        .build();
  }

  protected Filter convertFilterRequestToFilterAndSetId(long filterId,
      FilterRequest filterRequest) {
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
        .isDefault(filterRequest.isDefault())
        .build();
  }

  protected Filter getFilterById(long id, String token) throws Exception {
    String response = mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + id)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToFilter(response);
  }

  protected List<Filter> callRestToGetAllFilters(String token) throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getFiltersFromResponse(response);
  }

  protected List<Currency> callRestToGetAllCurrencies(String token) throws Exception {
    String response = mockMvc.perform(get(CURRENCIES_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCurrenciesFromResponse(response);
  }

  private Filter jsonToFilter(String jsonFilter) throws Exception {
    return mapper.readValue(jsonFilter, Filter.class);
  }

  private List<Filter> getFiltersFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Filter.class));
  }

  private List<Currency> getCurrenciesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Currency.class));
  }

  public long callRestToRegisterUserAndReturnUserId(User user) throws Exception {
    String response =
        mockMvc
            .perform(post(USERS_SERVICE_PATH + "/register")
                .content(json(user))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return Long.parseLong(response);
  }

  public String callRestToAuthenticateUserAndReturnToken(User user) throws Exception {
    String response = mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    return jsonToAuthResponse(response).getAccessToken().getValue();
  }

  public Tokens callRestToAuthenticateUserAndReturnTokens(User user) throws Exception {
    String response = mockMvc.perform(post(USERS_SERVICE_PATH + "/authenticate")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(user)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    UserDetails authResponse = jsonToAuthResponse(response);

    return new Tokens(
        authResponse.getId(),

        Token.builder()
            .userId(userId)
            .value(authResponse.getAccessToken().getValue())
            .expiryDate(authResponse.getAccessToken().getExpiryDate())
            .build(),

        Token.builder()
            .userId(userId)
            .value(authResponse.getRefreshToken().getValue())
            .expiryDate(authResponse.getRefreshToken().getExpiryDate())
            .build()
    );

  }

  protected int callRestToSetPlannedTransactionAsRecurrentAndReturnStatus(long transactionId,
      RecurrencePeriod recurrencePeriod) throws Exception {
    return callRestToSetPlannedTransactionRecurrentState(transactionId, SET_AS_RECURRENT,
        recurrencePeriod);
  }

  private int callRestToSetPlannedTransactionRecurrentState(long transactionId, String uriEnd,
      RecurrencePeriod recurrencePeriod)
      throws Exception {
    return mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + transactionId + uriEnd)
            .header(HttpHeaders.AUTHORIZATION, token)
            .param("recurrencePeriod", String.valueOf(recurrencePeriod))
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  protected int callRestToCommitPlannedTransaction(long plannedTransactionId) throws Exception {
    return mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + plannedTransactionId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  protected int callRestToCommitOverduePlannedTransaction(long plannedTransactionId)
      throws Exception {
    return mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + plannedTransactionId + COMMIT_OVERDUE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  public String callRestToRegisterAndAuthenticateUserAndReturnUserToken(User user)
      throws Exception {
    callRestToRegisterUserAndReturnUserId(user);
    return callRestToAuthenticateUserAndReturnToken(user);
  }

  protected int callRestToSetFilterAsDefaultAndReturnResponseStatus(long filterId) throws Exception {
    return mockMvc.perform(
        patch(FILTERS_SERVICE_PATH + "/" + filterId + SET_AS_DEFAULT)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  protected int callRestToSetFilterAsNotDefault(long filterId) throws Exception {
    return mockMvc.perform(
        patch(FILTERS_SERVICE_PATH + "/" + filterId + SET_AS_NOT_DEFAULT)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andReturn().getResponse().getStatus();
  }

  private UserDetails jsonToAuthResponse(String jsonAuthResponse) throws Exception {
    return mapper.readValue(jsonAuthResponse, UserDetails.class);
  }

}
