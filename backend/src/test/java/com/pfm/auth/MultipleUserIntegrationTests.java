package com.pfm.auth;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.FILTER_ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.FILTER_CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountIdeaBalance100000;
import static com.pfm.helpers.TestAccountProvider.accountIngBalance9999;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestAccountProvider.accountMilleniumBalance100;
import static com.pfm.helpers.TestCategoryProvider.categoryAnimals;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestFilterProvider.convertIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterExpensesOver1000;
import static com.pfm.helpers.TestFilterProvider.filterHomeExpensesUpTo200;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.animalsTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.homeTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.category.Category;
import com.pfm.category.CategoryRequest;
import com.pfm.currency.Currency;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterRequest;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class MultipleUserIntegrationTests extends IntegrationTestsBase {

  @Test
  public void shouldReturnErrorCausedByWrongUserAccountAndCategoryAddedToFilter() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    final String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));
    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);

    //when
    FilterRequest filterToAdd = convertFilterToFilterRequest(filterExpensesOver1000());
    filterToAdd.setCategoryIds(convertIdsToList(marianCategoryFoodId));
    filterToAdd.setAccountIds(convertIdsToList(marianAccountMbankId));

    mockMvc
        .perform(post(FILTERS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(filterToAdd))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(FILTER_ACCOUNT_ID_DOES_NOT_EXIST), marianAccountMbankId))))
        .andExpect(jsonPath("$[1]", is(String.format(getMessage(FILTER_CATEGORY_ID_DOES_NOT_EXIST), marianCategoryFoodId))));
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserAccountAndCategoryAddedToFilterInUpdateMethod() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    final String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    long zdzislawUserId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    final long marianOver1000ExpensesFilter = callRestServiceToAddFilterAndReturnId(filterExpensesOver1000(), marianToken);

    Account account = accountIdeaBalance100000();
    account.setCurrency(currencyService.getCurrencies(zdzislawUserId).get(0));

    long zdzislawAccountIdeaId = callRestServiceToAddAccountAndReturnId(account, zdzislawToken);
    long zdzislawCategoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), zdzislawToken);

    //when
    FilterRequest updatedFilter = convertFilterToFilterRequest(filterExpensesOver1000());
    updatedFilter.setAccountIds(convertIdsToList(zdzislawAccountIdeaId));
    updatedFilter.setCategoryIds(convertIdsToList(zdzislawCategoryHomeId));

    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + marianOver1000ExpensesFilter)
            .header(HttpHeaders.AUTHORIZATION, marianToken)
            .content(json(updatedFilter))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(FILTER_ACCOUNT_ID_DOES_NOT_EXIST), zdzislawAccountIdeaId))))
        .andExpect(jsonPath("$[1]", is(String.format(getMessage(FILTER_CATEGORY_ID_DOES_NOT_EXIST), zdzislawCategoryHomeId))));
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToUpdateFilter() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianExpensesOver1000Filter = callRestServiceToAddFilterAndReturnId(filterExpensesOver1000(), marianToken);

    //when
    FilterRequest updatedFilter = convertFilterToFilterRequest(filterExpensesOver1000());
    updatedFilter.setName("updated name");

    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + marianExpensesOver1000Filter)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(updatedFilter))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToDelteFilter() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());
    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianExpensesOver1000Filter = callRestServiceToAddFilterAndReturnId(filterExpensesOver1000(), marianToken);

    //when
    mockMvc
        .perform(delete(FILTERS_SERVICE_PATH + "/" + marianExpensesOver1000Filter)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToUpdateTransaction() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);
    final long marianFoodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), marianAccountMbankId,
        marianCategoryFoodId, marianToken);

    //when
    TransactionRequest updatedTransaction = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updatedTransaction.getAccountPriceEntries().get(0).setAccountId(marianAccountMbankId);
    updatedTransaction.setCategoryId(marianCategoryFoodId);
    updatedTransaction.setDescription("updated descrition");

    mockMvc
        .perform(put(TRANSACTIONS_SERVICE_PATH + "/" + marianFoodTransactionId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(updatedTransaction))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToDeleteTransaction() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);
    long marianFoodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), marianAccountMbankId,
        marianCategoryFoodId, marianToken);

    //when
    mockMvc
        .perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + marianFoodTransactionId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToUpdateCategory() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);

    //when
    CategoryRequest updatedCategory = CategoryRequest.builder()
        .name("updatedCategory")
        .build();

    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + marianCategoryFoodId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(updatedCategory))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToDeleteCategory() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);

    //when
    mockMvc
        .perform(delete(CATEGORIES_SERVICE_PATH + "/" + marianCategoryFoodId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToUpdateAccount() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);

    //when
    AccountRequest updatedAccount = AccountRequest.builder()
        .name("updated name")
        .balance(convertDoubleToBigDecimal(123))
        .build();

    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + marianAccountMbankId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(updatedAccount))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserTryingToDeleteAccount() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);

    //when
    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + marianAccountMbankId)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserCategoryAndWrongUserCategoryAddedToTransaction() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);
    long marianCategoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), marianToken);

    //when
    TransactionRequest transactionToAdd = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    transactionToAdd.setCategoryId(marianCategoryCarId);
    transactionToAdd.getAccountPriceEntries().get(0).setAccountId(marianAccountMbankId);

    mockMvc
        .perform(post(TRANSACTIONS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
            .content(json(transactionToAdd))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(CATEGORY_ID_DOES_NOT_EXIST), marianCategoryCarId))))
        .andExpect(jsonPath("$[1]", is(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), marianAccountMbankId))));
  }

  @Test
  public void shouldReturnErrorCausedByWrongUserCategoryAndWrongUserCategoryAddedToTransactionInUpdateMethod() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    long zdzislawUserId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(account, marianToken);
    long marianCategoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), marianToken);
    final long marianFoodTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), marianAccountMbankId,
        marianCategoryCarId, marianToken);

    Account zdzislawAccount = accountIdeaBalance100000();
    zdzislawAccount.setCurrency(currencyService.getCurrencies(zdzislawUserId).get(0));

    long zdzislawAccountIdeaId = callRestServiceToAddAccountAndReturnId(zdzislawAccount, zdzislawToken);
    long zdzislawCategoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), zdzislawToken);

    //when
    TransactionRequest updatedTransaction = convertTransactionToTransactionRequest(carTransactionWithNoAccountAndNoCategory());
    updatedTransaction.setCategoryId(zdzislawCategoryHomeId);
    updatedTransaction.getAccountPriceEntries().get(0).setAccountId(zdzislawAccountIdeaId);

    mockMvc
        .perform(put(TRANSACTIONS_SERVICE_PATH + "/" + marianFoodTransactionId)
            .header(HttpHeaders.AUTHORIZATION, marianToken)
            .content(json(updatedTransaction))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(String.format(getMessage(CATEGORY_ID_DOES_NOT_EXIST), zdzislawCategoryHomeId))))
        .andExpect(jsonPath("$[1]", is(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST), zdzislawAccountIdeaId))));
  }

  @Test
  public void shouldReturnUnauthorizedCausedByWrongToken() throws Exception {
    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, "Wrong accessToken")
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedCausedByEmptyToken() throws Exception {
    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, "")
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedCausedByNullToken() throws Exception {
    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void optionsRequestTest() throws Exception {
    mockMvc
        .perform(options(ACCOUNTS_SERVICE_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldRegisterTwoUsersAndAddAccountsCategoriesTransaction() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    currencyService.addDefaultCurrencies(marianUserId);
    final String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    long zdzislawUserId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    currencyService.addDefaultCurrencies(zdzislawUserId);
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    //when

    //marian
    Currency marianAccountCurrency = currencyService.getCurrencies(marianUserId).get(0);

    Account accountMbankMarian = accountMbankBalance10();
    accountMbankMarian.setCurrency(marianAccountCurrency);
    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(accountMbankMarian, marianToken);

    Account accountMilleniumMarian = accountMilleniumBalance100();
    accountMilleniumMarian.setCurrency(marianAccountCurrency);
    long marianAccountMilleniumId = callRestServiceToAddAccountAndReturnId(accountMilleniumMarian, marianToken);

    long marianCategoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), marianToken);
    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);
    final long marianFoodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), marianAccountMbankId,
        marianCategoryFoodId, marianToken);
    final long marianCarTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), marianAccountMilleniumId,
        marianCategoryCarId, marianToken);

    Filter filterExpensesOver1000ToAdd = filterExpensesOver1000();
    filterExpensesOver1000ToAdd.setCategoryIds(convertIdsToList(marianCategoryFoodId, marianCategoryCarId));
    filterExpensesOver1000ToAdd.setAccountIds(convertIdsToList(marianAccountMilleniumId, marianAccountMbankId));
    final long marianExpensesOver1000FilterId = callRestServiceToAddFilterAndReturnId(filterExpensesOver1000ToAdd, marianToken);

    //zdzislaw
    Currency zdzislawAccountCurrency = currencyService.getCurrencies(zdzislawUserId).get(1);

    Account accountIngZdzislaw = accountIngBalance9999();
    accountIngZdzislaw.setCurrency(zdzislawAccountCurrency);
    long zdzislawAccountIngId = callRestServiceToAddAccountAndReturnId(accountIngZdzislaw, zdzislawToken);

    Account accountIdeaZdzislaw = accountIdeaBalance100000();
    accountIdeaZdzislaw.setCurrency(zdzislawAccountCurrency);
    long zdzislawAccountIdeaId = callRestServiceToAddAccountAndReturnId(accountIdeaZdzislaw, zdzislawToken);

    long zdzislawCategoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), zdzislawToken);
    long zdzislawCategoryAnimalsId = callRestToAddCategoryAndReturnId(categoryAnimals(), zdzislawToken);
    final long zdzislawTransactionAnimalsId = callRestToAddTransactionAndReturnId(animalsTransactionWithNoAccountAndNoCategory(),
        zdzislawAccountIngId,
        zdzislawCategoryAnimalsId, zdzislawToken);
    final long zdzislawTransactionHomeId = callRestToAddTransactionAndReturnId(homeTransactionWithNoAccountAndNoCategory(), zdzislawAccountIdeaId,
        zdzislawCategoryHomeId, zdzislawToken);

    Filter filterHomeExpensesToAdd = filterHomeExpensesUpTo200();
    filterHomeExpensesToAdd.setAccountIds(convertIdsToList(zdzislawAccountIngId, zdzislawAccountIdeaId));
    filterHomeExpensesToAdd.setCategoryIds(convertIdsToList(zdzislawCategoryHomeId));
    final long zdzislawHomeExpensesFilterId = callRestServiceToAddFilterAndReturnId(filterHomeExpensesToAdd, zdzislawToken);

    //then
    final List<Account> accountsMarian = callRestToGetAllAccounts(marianToken);

    marianAccountCurrency.setUserId(null); // services do not return userId - it's private information

    Account marianAccountMbankExpected = accountMbankBalance10();
    marianAccountMbankExpected.setId(marianAccountMbankId);
    marianAccountMbankExpected.setCurrency(marianAccountCurrency);
    marianAccountMbankExpected
        .setBalance(accountMbankBalance10().getBalance().add(foodTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice()));

    Account marianAccountMilleniumExpected = accountMilleniumBalance100();
    marianAccountMilleniumExpected.setId(marianAccountMilleniumId);
    marianAccountMilleniumExpected.setCurrency(marianAccountCurrency);
    marianAccountMilleniumExpected
        .setBalance(
            accountMilleniumBalance100().getBalance().add(carTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice()));

    assertThat(accountsMarian, hasSize(2));
    assertThat(accountsMarian, containsInAnyOrder(marianAccountMbankExpected, marianAccountMilleniumExpected));

    final List<Account> accountsZdzislaw = callRestToGetAllAccounts(zdzislawToken);

    zdzislawAccountCurrency.setUserId(null); // services do not return userId - it's private information

    Account zdzislawAccountIngExpected = accountIngBalance9999();
    zdzislawAccountIngExpected.setId(zdzislawAccountIngId);
    zdzislawAccountIngExpected.setCurrency(zdzislawAccountCurrency);
    zdzislawAccountIngExpected.setBalance(
        accountIngBalance9999().getBalance().add(animalsTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice()));

    Account zdzislawAccountIdeaExpected = accountIdeaBalance100000();
    zdzislawAccountIdeaExpected.setId(zdzislawAccountIdeaId);
    zdzislawAccountIdeaExpected.setCurrency(zdzislawAccountCurrency);
    zdzislawAccountIdeaExpected.setBalance(
        accountIdeaBalance100000().getBalance().add(homeTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice()));

    assertThat(accountsZdzislaw, hasSize(2));
    assertThat(accountsZdzislaw, containsInAnyOrder(zdzislawAccountIngExpected, zdzislawAccountIdeaExpected));

    final List<Category> marianCategories = callRestToGetAllCategories(marianToken);

    Category marianCategoryCarExpected = categoryCar();
    marianCategoryCarExpected.setId(marianCategoryCarId);

    Category marianCategoryFoodExpected = categoryFood();
    marianCategoryFoodExpected.setId(marianCategoryFoodId);

    assertThat(marianCategories, hasSize(2));
    assertThat(marianCategories, containsInAnyOrder(marianCategoryCarExpected, marianCategoryFoodExpected));

    final List<Category> zdzislawCategories = callRestToGetAllCategories(zdzislawToken);

    Category zdzislawCategoryHomeExpected = categoryHome();
    zdzislawCategoryHomeExpected.setId(zdzislawCategoryHomeId);

    Category zdzislawCategoryAnimalsExpected = categoryAnimals();
    zdzislawCategoryAnimalsExpected.setId(zdzislawCategoryAnimalsId);

    assertThat(zdzislawCategories, hasSize(2));
    assertThat(zdzislawCategories, containsInAnyOrder(zdzislawCategoryAnimalsExpected, zdzislawCategoryHomeExpected));

    final List<Transaction> marianTransactions = callRestToGetAllTransactionsFromDatabase(marianToken);

    Transaction marianFoodTransactionExpected = foodTransactionWithNoAccountAndNoCategory();
    marianFoodTransactionExpected.setId(marianFoodTransactionId);
    marianFoodTransactionExpected.getAccountPriceEntries().get(0).setAccountId(marianAccountMbankId);
    marianFoodTransactionExpected.setCategoryId(marianCategoryFoodId);

    Transaction marianCarTransactionExpected = carTransactionWithNoAccountAndNoCategory();
    marianCarTransactionExpected.setId(marianCarTransactionId);
    marianCarTransactionExpected.getAccountPriceEntries().get(0).setAccountId(marianAccountMilleniumId);
    marianCarTransactionExpected.setCategoryId(marianCategoryCarId);

    assertThat(marianTransactions, hasSize(2));
    assertThat(marianTransactions, containsInAnyOrder(marianFoodTransactionExpected, marianCarTransactionExpected));

    final List<Transaction> zdzislawTransactions = callRestToGetAllTransactionsFromDatabase(zdzislawToken);

    Transaction zdzislawAnimalsTransactionsExpected = animalsTransactionWithNoAccountAndNoCategory();
    zdzislawAnimalsTransactionsExpected.setId(zdzislawTransactionAnimalsId);
    zdzislawAnimalsTransactionsExpected.getAccountPriceEntries().get(0).setAccountId(zdzislawAccountIngId);
    zdzislawAnimalsTransactionsExpected.setCategoryId(zdzislawCategoryAnimalsId);

    Transaction zdzislawHomeTransactionsExpected = homeTransactionWithNoAccountAndNoCategory();
    zdzislawHomeTransactionsExpected.setId(zdzislawTransactionHomeId);
    zdzislawHomeTransactionsExpected.getAccountPriceEntries().get(0).setAccountId(zdzislawAccountIdeaId);
    zdzislawHomeTransactionsExpected.setCategoryId(zdzislawCategoryHomeId);

    assertThat(zdzislawTransactions, hasSize(2));
    assertThat(zdzislawTransactions, containsInAnyOrder(zdzislawAnimalsTransactionsExpected, zdzislawHomeTransactionsExpected));

    List<Filter> marianFilters = callRestToGetAllFilters(marianToken);
    filterExpensesOver1000ToAdd.setId(marianExpensesOver1000FilterId);

    assertThat(marianFilters, hasSize(1));
    assertThat(marianFilters, containsInAnyOrder(filterExpensesOver1000ToAdd));

    List<Filter> zdzislawFilters = callRestToGetAllFilters(zdzislawToken);
    filterHomeExpensesToAdd.setId(zdzislawHomeExpensesFilterId);

    assertThat(zdzislawFilters, hasSize(1));
    assertThat(zdzislawFilters, containsInAnyOrder(filterHomeExpensesToAdd));
  }

  @Test
  public void shouldAccountWithTheSameNameToDifferentUsers() throws Exception {
    //given
    long marianUserId = callRestToRegisterUserAndReturnUserId(userMarian());
    final String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());
    currencyService.addDefaultCurrencies(marianUserId);

    long zdzislawUserId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    final String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());
    currencyService.addDefaultCurrencies(zdzislawUserId);

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(marianUserId).get(0));

    //when
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, marianToken)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(account))))
        .andExpect(status().isOk());

    account.setCurrency(currencyService.getCurrencies(zdzislawUserId).get(0));

    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(account))))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldAddCategoryWithTheSameNameToDifferentUsers() throws Exception {
    //given
    callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());

    callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    Category category = categoryCar();

    //when
    mockMvc.perform(post(CATEGORIES_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, marianToken)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertCategoryToCategoryRequest(category))))
        .andExpect(status().isOk());

    mockMvc.perform(post(CATEGORIES_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, zdzislawToken)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertCategoryToCategoryRequest(category))))
        .andExpect(status().isOk());
  }
}
