package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_ARCHIVED;
import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.FUTURE_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.PAST_PLANNED_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.filters.LanguageFilter.LANGUAGE_HEADER;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carPlannedTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodPlannedTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.transaction.TransactionController.RECURRENCE_PERIOD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.config.MessagesProvider.Language;
import com.pfm.currency.Currency;
import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class TransactionControllerIntegrationTest extends IntegrationTestsBase {

  private static final long NOT_EXISTING_TRANSACTION_ID = -7389387L;
  private static final String EMPTY_DESCRIPTION = "";

  @Autowired
  private AccountService accountService;

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    BigDecimal expectedPrice = accountJacekBalance1000().getBalance()
        .add(foodTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice());

    //when
    long transactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId, token);

    //then
    Transaction expectedTransaction =
        setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), transactionId, jacekAccountId, foodCategoryId);

    assertThat(callRestToGetTransactionById(transactionId, token), is(equalTo(expectedTransaction)));
    BigDecimal jacekAccountBalanceAfterAddingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);

    assertThat(jacekAccountBalanceAfterAddingTransaction, is(expectedPrice));
  }

  @Test
  public void shouldDeleteTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Transaction transactionToAdd = foodTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(transactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedTransaction = foodTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(addedTransaction, transactionId, jacekAccountId, foodCategoryId);

    //when
    callRestToDeleteTransactionById(transactionId, token);

    //then
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase(token);

    assertThat(allTransactionsInDb.size(), is(0));
    assertThat(allTransactionsInDb.contains(addedTransaction), is(false));

    BigDecimal jacekAccountBalanceAfterDeletingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterDeletingTransaction,
        is(accountJacekBalance1000().getBalance()));
  }

  @Test
  public void shouldGetTransactions() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId, token);
    long carTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), jacekAccountId, carCategoryId, token);

    //when
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase(token);

    //then
    Transaction foodTransactionExpected =
        setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), foodTransactionId, jacekAccountId, foodCategoryId);

    Transaction carTransactionExpected =
        setTransactionIdAccountIdCategoryId(carTransactionWithNoAccountAndNoCategory(), carTransactionId, jacekAccountId, carCategoryId);

    assertThat(allTransactionsInDb.size(), is(2));
    assertThat(allTransactionsInDb, containsInAnyOrder(foodTransactionExpected, carTransactionExpected));

  }

  @Test
  public void shouldUpdateTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(0));
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    TransactionRequest updatedFoodTransactionRequest = helper.convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updatedFoodTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodTransactionRequest.setDate(updatedFoodTransactionRequest.getDate().plusDays(1));
    updatedFoodTransactionRequest.setDescription("Car parts");

    //when
    callRestToUpdateTransaction(foodTransactionId, updatedFoodTransactionRequest, token);

    //then
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(allTransactionsInDb.size(), is(1));

    Transaction expected = convertTransactionRequestToTransactionAndSetId(foodTransactionId, updatedFoodTransactionRequest);

    assertThat(allTransactionsInDb, contains(expected));

    BigDecimal jacekAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterTransactionUpdate, is(accountJacekBalance1000().getBalance()));

    BigDecimal piotrAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(mbankAccountId, token);
    assertThat(piotrAccountBalanceAfterTransactionUpdate,
        is(accountMbankBalance10().getBalance().add(updatedFoodTransactionRequest.getAccountPriceEntries().get(0).getPrice())));
  }

  @Test
  public void shouldReturnErrorWhenTryingToDeleteAccountUsedInTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    mockMvc.perform(delete(ACCOUNTS_SERVICE_PATH + "/" + jacekAccountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(ACCOUNT_IS_USED_IN_TRANSACTION))));
  }

  @Test
  public void shouldReturnErrorWhenTryingToDeleteCategoryUsedInTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + foodCategoryId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(CATEGORY_IS_USED_IN_TRANSACTION))));
  }

  @Test
  public void shouldReturnValidationErrorInUpdateMethod() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);
    TransactionRequest updateFoodTransaction = helper.convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updateFoodTransaction.setCategoryId(foodCategoryId);
    updateFoodTransaction.getAccountPriceEntries().get(0).setAccountId(jacekAccountId);
    updateFoodTransaction.setDate(null);

    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + foodTransactionId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .content(json(updateFoodTransaction))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_DATE))));
  }

  @Test
  public void shouldReturnValidationErrorForArchivedAccount() throws Exception {
    //given
    Account account = Account.builder()
        .name("Jacek Millenium Bank savings")
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder()
            .id(currencyService.getCurrencies(userId).get(0).getId())
            .name("USD")
            .exchangeRate(BigDecimal.valueOf(3.99))
            .build())
        .build();

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToMarkAccountAsArchived(accountId);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction transaction = Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .accountId(accountId)
                .price(convertDoubleToBigDecimal(10))
                .build())
        )
        .description("Food for birthday")
        .categoryId(foodCategoryId)
        .date(LocalDate.of(2018, 8, 8))
        .build();
    TransactionRequest transactionRequest = helper.convertTransactionToTransactionRequest(transaction);
    mockMvc
        .perform(post(TRANSACTIONS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(transactionRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(ACCOUNT_IS_ARCHIVED, Language.ENGLISH))));

  }

  @Test
  public void shouldReturnErrorCausedByEmptyFields() throws Exception {
    //given
    TransactionRequest transactionToAdd = new TransactionRequest();

    //when
    mockMvc.perform(post(TRANSACTIONS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .header(LANGUAGE_HEADER, "de") // will default to en
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_NAME, Language.ENGLISH))))
        .andExpect(jsonPath("$[1]", Matchers.is(getMessage(EMPTY_TRANSACTION_CATEGORY, Language.ENGLISH))))
        .andExpect(jsonPath("$[2]", Matchers.is(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED, Language.ENGLISH))))
        .andExpect(jsonPath("$[3]", Matchers.is(getMessage(EMPTY_TRANSACTION_DATE, Language.ENGLISH))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyDescription() throws Exception {
    //given
    TransactionRequest transactionToAdd = TransactionRequest.builder()
        .description(" ")
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .accountId(NOT_EXISTING_ID)
                .price(convertDoubleToBigDecimal(10))
                .build())
        )
        .categoryId(NOT_EXISTING_ID)
        .date(LocalDate.of(2018, 10, 10))
        .build();

    //when
    mockMvc.perform(post(TRANSACTIONS_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .header(LANGUAGE_HEADER, "pl")
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_NAME, Language.POLISH))))
        .andExpect(jsonPath("$[1]", Matchers.is(String.format(getMessage(CATEGORY_ID_DOES_NOT_EXIST, Language.POLISH), NOT_EXISTING_ID))))
        .andExpect(jsonPath("$[2]", Matchers.is(String.format(getMessage(ACCOUNT_ID_DOES_NOT_EXIST, Language.POLISH), NOT_EXISTING_ID))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInGetMethod() throws Exception {
    //when
    mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInDeleteMethod() throws Exception {
    //when
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInUpdateMethod() throws Exception {
    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header(HttpHeaders.AUTHORIZATION, token)
        .content(json(TransactionRequest.builder().build()))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldAddPlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction plannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    plannedTransaction.setPlanned(true);

    //when
    long plannedTransactionId = callRestToAddTransactionAndReturnId(plannedTransaction, jacekAccountId, foodCategoryId, token);

    //then
    Transaction expectedPlannedTransaction =
        setTransactionIdAccountIdCategoryId(foodPlannedTransactionWithNoAccountAndNoCategory(), plannedTransactionId, jacekAccountId,
            foodCategoryId);

    assertThat(callRestToGetTransactionById(plannedTransactionId, token), is(equalTo(expectedPlannedTransaction)));

  }

  @Test
  public void shouldDeletePlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Transaction plannedTransactionToAdd = foodPlannedTransactionWithNoAccountAndNoCategory();
    long plannedTransactionId = callRestToAddTransactionAndReturnId(plannedTransactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedPlannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(addedPlannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);

    //when
    callRestToDeletePlannedTransactionById(plannedTransactionId, token);

    //then
    List<Transaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    assertThat(allPlannedTransactionsInDb.size(), is(0));
    assertThat(allPlannedTransactionsInDb.contains(addedPlannedTransaction), is(false));

    BigDecimal jacekAccountBalanceAfterDeletingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterDeletingTransaction,
        is(accountJacekBalance1000().getBalance()));
  }

  @Test
  public void shouldGetPlannedTransactions() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long foodPlannedTransactionId = callRestToAddTransactionAndReturnId(
        foodPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId, token);
    long carPlannedTransactionId = callRestToAddTransactionAndReturnId(
        carPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId, carCategoryId, token);

    //when
    List<Transaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    //then
    Transaction foodPlannedTransactionExpected =
        setTransactionIdAccountIdCategoryId(foodPlannedTransactionWithNoAccountAndNoCategory(),
            foodPlannedTransactionId, jacekAccountId, foodCategoryId);

    Transaction carPlannedTransactionExpected =
        setTransactionIdAccountIdCategoryId(carPlannedTransactionWithNoAccountAndNoCategory(),
            carPlannedTransactionId, jacekAccountId, carCategoryId);

    assertThat(allPlannedTransactionsInDb.size(), is(2));
    assertThat(allPlannedTransactionsInDb, containsInAnyOrder(foodPlannedTransactionExpected, carPlannedTransactionExpected));

  }

  @Test
  public void shouldUpdatePlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(0));
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long foodPlannedTransactionId = callRestToAddTransactionAndReturnId(foodPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId,
        foodCategoryId, token);

    Transaction updatedPlannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    updatedPlannedTransaction.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedPlannedTransaction.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedPlannedTransaction.setCategoryId(carCategoryId);
    updatedPlannedTransaction.setDate(updatedPlannedTransaction.getDate().plusDays(1));
    updatedPlannedTransaction.setDescription("Car parts");

    TransactionRequest updatedPlannedTransactionRequest = helper.convertTransactionToTransactionRequest(updatedPlannedTransaction);
    updatedPlannedTransaction.setId(1L);

    //when
    callRestToUpdateTransaction(foodPlannedTransactionId, updatedPlannedTransactionRequest, token);
    List<Transaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    //then
    assertThat(allPlannedTransactionsInDb.size(), is(1));
    assertThat(allPlannedTransactionsInDb.get(0), equalTo(updatedPlannedTransaction));

  }

  @Test
  public void shouldReturnNotFoundDuringGettingNotExistingPlannedTransaction() throws Exception {
    //given
    final long notExistingPlannedTransactionId = 790;
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);

    callRestToAddTransactionAndReturnId(
        foodPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId, token);
    callRestToAddTransactionAndReturnId(
        carTransactionWithNoAccountAndNoCategory(), jacekAccountId, carCategoryId, token);

    mockMvc
        .perform(get(TRANSACTIONS_SERVICE_PATH + "/" + notExistingPlannedTransactionId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        //then
        .andExpect(status().isNotFound());

  }

  @Test
  public void shouldReturnBadRequestDuringAddingPlannedTransactionCausedByCategoryNotPassingTransactionValidation() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    Transaction invalidPlannedTransaction = Transaction.builder()
        .description("Fuel")
        .build();

    mockMvc
        .perform(
            post(TRANSACTIONS_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(invalidPlannedTransaction))
                .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void shouldReturnBadRequestDuringAddingTransactionToArchivedAccount() throws Exception {
    //given
    Account account = Account.builder()
        .name("Jacek Millenium Bank savings")
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder()
            .id(currencyService.getCurrencies(userId).get(0).getId())
            .name("USD")
            .exchangeRate(BigDecimal.valueOf(3.99))
            .build())
        .build();

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToMarkAccountAsArchived(accountId);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction plannedTransaction = Transaction.builder()
        .accountPriceEntries(Collections.singletonList(
            AccountPriceEntry.builder()
                .accountId(accountId)
                .price(convertDoubleToBigDecimal(10))
                .build())
        )
        .description("Food for birthday")
        .categoryId(foodCategoryId)
        .date(LocalDate.of(2018, 8, 8))
        .build();
    TransactionRequest plannedTransactionRequest = helper.convertTransactionToTransactionRequest(plannedTransaction);
    mockMvc
        .perform(post(TRANSACTIONS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(plannedTransactionRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(ACCOUNT_IS_ARCHIVED, Language.ENGLISH))));

  }

  @Test
  public void shouldReturnNotFoundDuringUpdatingNotExistingPlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(0));
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    final long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    callRestToAddTransactionAndReturnId(
        foodPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    TransactionRequest updatedFoodPlannedTransactionRequest = helper.convertTransactionToTransactionRequest(
        foodPlannedTransactionWithNoAccountAndNoCategory());
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription("Car parts");

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_TRANSACTION_ID)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedFoodPlannedTransactionRequest)))

        //then
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnBadRequestDuringUpdatingPlannedTransactionWithOneThatDoesNotPassValidation() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(0));
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long transactionId = callRestToAddTransactionAndReturnId(
        foodPlannedTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    TransactionRequest updatedFoodPlannedTransactionRequest = helper.convertTransactionToTransactionRequest(
        foodPlannedTransactionWithNoAccountAndNoCategory());
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription(EMPTY_DESCRIPTION);

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + transactionId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedFoodPlannedTransactionRequest)))

        //then
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnNotFoundDuringDeletingNotExistingPlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Transaction plannedTransactionToAdd = foodPlannedTransactionWithNoAccountAndNoCategory();
    long plannedTransactionId = callRestToAddTransactionAndReturnId(plannedTransactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedPlannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(addedPlannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);

    //when
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_TRANSACTION_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldCommitPlannedTransaction() throws Exception {
    //given
    final long plannedTransactionId = callRestToAddFirstTestPlannedTransactionAndReturnId();
    Transaction expectedPlannedTransactionAfterCommit = callRestToGetTransactionById(plannedTransactionId, token);

    List<Transaction> allTransactions = callRestToGetAllTransactionsFromDatabase(token);
    List<Transaction> allPlannedTransactions = callRestToGetAllPlannedTransactionsFromDatabase(token);

    assertThat(allTransactions.size(), is(0));
    assertThat(allPlannedTransactions.size(), is(1));
    assertThat(allPlannedTransactions.get(0), is(equalTo(expectedPlannedTransactionAfterCommit)));

    //when
    callRestToCommitPlannedTransaction(plannedTransactionId);
    final List<Transaction> allTransactionsAfterCommit = callRestToGetAllTransactionsFromDatabase(token);
    final List<Transaction> allPlannedTransactionsAfterCommit = callRestToGetAllPlannedTransactionsFromDatabase(token);

    expectedPlannedTransactionAfterCommit.setDate(LocalDate.now());
    expectedPlannedTransactionAfterCommit.setPlanned(false);

    //then
    assertThat(allTransactionsAfterCommit.size(), is(1));
    assertThat(allPlannedTransactionsAfterCommit.size(), is(0));
    assertThat(removeTransactionId(allTransactionsAfterCommit.get(0)), is(equalTo(removeTransactionId(expectedPlannedTransactionAfterCommit))));

    expectedPlannedTransactionAfterCommit.setPlanned(false);
  }

  @Test
  public void shouldReturnValidationErrorForTransactionWithFutureDate() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction transaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    transaction.setPlanned(false);

    TransactionRequest transactionRequest = helper.convertTransactionToTransactionRequest(transaction);

    transactionRequest.setCategoryId(foodCategoryId);
    transactionRequest.getAccountPriceEntries().get(0).setAccountId(jacekAccountId);
    mockMvc
        .perform(post(TRANSACTIONS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(transactionRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(FUTURE_TRANSACTION_DATE))));

  }

  @Test
  public void shouldReturnValidationErrorForPlannedTransactionWithPastDate() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setPlanned(true);

    TransactionRequest transactionRequest = helper.convertTransactionToTransactionRequest(transaction);

    transactionRequest.setCategoryId(foodCategoryId);
    transactionRequest.getAccountPriceEntries().get(0).setAccountId(jacekAccountId);
    mockMvc
        .perform(post(TRANSACTIONS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(transactionRequest))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(PAST_PLANNED_TRANSACTION_DATE))));

  }

  @Test
  public void shouldReturnNotFoundForNotDuringCommittingNotExistingPlannedTransaction() throws Exception {
    //given
    mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_TRANSACTION_ID)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());

  }

  @Test
  public void shouldReturnBadRequestDuringCommittingPlannedTransactionWithArchivedAccount() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction plannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    plannedTransaction.setPlanned(true);

    long plannedTransactionId = callRestToAddTransactionAndReturnId(plannedTransaction, jacekAccountId, foodCategoryId, token);

    //when
    callRestToMarkAccountAsArchived(jacekAccountId);

    //then
    mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + plannedTransactionId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(ACCOUNT_IS_ARCHIVED))));

  }

  @Test
  public void shouldSetPlannedTransactionAsRecurrent() throws Exception {
    //given
    long transactionId = callRestToAddFirstTestPlannedTransactionAndReturnId();
    Transaction addedTransaction = callRestToGetTransactionById(transactionId, token);

    final boolean recurrent = addedTransaction.isRecurrent();
    assertThat(addedTransaction, is(not(recurrent)));

    //when
    mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + transactionId + SET_AS_RECURRENT)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    Transaction updatedTransaction = callRestToGetTransactionById(transactionId, token);

    final boolean recurrentAfterUpdate = updatedTransaction.isRecurrent();
    //then
    assertThat(recurrentAfterUpdate, is(true));

  }

  @Test
  public void shouldSetPlannedTransactionAsNotRecurrent() throws Exception {
    //given
    long transactionId = callRestToAddFirstTestPlannedTransactionAndReturnId();
    Transaction addedTransaction = callRestToGetTransactionById(transactionId, token);

    final boolean recurrent = addedTransaction.isRecurrent();
    assertThat(addedTransaction, is(not(recurrent)));

    int status = callRestToSetPlannedTransactionAsRecurrentAndReturnStatus(transactionId);

    assertThat(status, is(OK.value()));

    Transaction updatedTransaction = callRestToGetTransactionById(transactionId, token);

    assertTrue(updatedTransaction.isRecurrent());

    //when
    status = callRestToSetPlannedTransactionAsNotRecurrentAndReturnStatus(transactionId);
    assertThat(status, is(OK.value()));

    Transaction transaction = callRestToGetTransactionById(transactionId, token);

    //then
    assertFalse(transaction.isRecurrent());
    assertThat(transaction, is(equalTo(addedTransaction)));

  }

  @Test
  public void shouldReturnNotFoundDuringMakingNotExistingTransactionRecurrent() throws Exception {

    int status = callRestToSetPlannedTransactionAsRecurrentAndReturnStatus(NOT_EXISTING_TRANSACTION_ID);

    assertThat(status, is(NOT_FOUND.value()));
  }

  @Test
  public void shouldAddPlannedTransactionForNextMonthDuringCommittingRecurrentTransaction() throws Exception {
    //given
    long plannedTransactionId = callRestToAddFirstTestPlannedTransactionAndReturnId();
    Transaction addedTransaction = callRestToGetTransactionById(plannedTransactionId, token);

    List<Transaction> allTransactions = callRestToGetAllTransactionsFromDatabase(token);
    List<Transaction> allPlannedTransactions = callRestToGetAllPlannedTransactionsFromDatabase(token);

    assertThat(allTransactions.size(), is(0));
    assertThat(allPlannedTransactions.size(), is(1));
    assertThat(allPlannedTransactions.get(0), is(equalTo(addedTransaction)));

    int status = callRestToSetPlannedTransactionAsRecurrentAndReturnStatus(plannedTransactionId);
    assertEquals(OK.value(), status);

    Transaction addedTransactionWithRecurrentStatus = callRestToGetTransactionById(plannedTransactionId, token);
    callRestToCommitPlannedTransaction(plannedTransactionId);

    final List<Transaction> allTransactionsAfterCommit = callRestToGetAllTransactionsFromDatabase(token);
    final List<Transaction> allPlannedTransactionsAfterCommit = callRestToGetAllPlannedTransactionsFromDatabase(token);

    final Transaction expectedNextRecurrentTransaction = Transaction.builder()
        .id(addedTransactionWithRecurrentStatus.getId())
        .description(addedTransactionWithRecurrentStatus.getDescription())
        .categoryId(addedTransactionWithRecurrentStatus.getCategoryId())
        .date(RECURRENCE_PERIOD)
        .accountPriceEntries(addedTransactionWithRecurrentStatus.getAccountPriceEntries())
        .userId(addedTransactionWithRecurrentStatus.getUserId())
        .isPlanned(addedTransactionWithRecurrentStatus.isPlanned())
        .isRecurrent(addedTransactionWithRecurrentStatus.isRecurrent())
        .build();

    assertThat(allTransactionsAfterCommit.size(), is(1));
    assertThat(allPlannedTransactionsAfterCommit.size(), is(1));
    assertThat(allPlannedTransactionsAfterCommit.get(0).getDate(), is(equalTo(RECURRENCE_PERIOD)));
    assertThat(removeTransactionId(allPlannedTransactionsAfterCommit.get(0)), is(equalTo(removeTransactionId(expectedNextRecurrentTransaction))));

  }

  @Test
  public void shouldCommitPlannedTransactionUsingOverdueEndpoint() throws Exception {
    //given
    final long plannedTransactionId = callRestToAddFirstTestPlannedTransactionAndReturnId();
    Transaction expectedPlannedTransactionAfterCommit = callRestToGetTransactionById(plannedTransactionId, token);

    List<Transaction> allTransactions = callRestToGetAllTransactionsFromDatabase(token);
    List<Transaction> allPlannedTransactions = callRestToGetAllPlannedTransactionsFromDatabase(token);
    assertThat(allTransactions.size(), is(0));
    assertThat(allPlannedTransactions.size(), is(1));
    assertThat(allPlannedTransactions.get(0), is(equalTo(expectedPlannedTransactionAfterCommit)));
    expectedPlannedTransactionAfterCommit.setDate(LocalDate.now());
    expectedPlannedTransactionAfterCommit.setPlanned(false);

    callRestToCommitOverduePlannedTransaction(plannedTransactionId);
    final List<Transaction> allTransactionsAfterCommit = callRestToGetAllTransactionsFromDatabase(token);
    final List<Transaction> allPlannedTransactionsAfterCommit = callRestToGetAllPlannedTransactionsFromDatabase(token);

    //then
    assertThat(allTransactionsAfterCommit.size(), is(1));
    assertThat(allPlannedTransactionsAfterCommit.size(), is(0));
    assertThat(removeTransactionId(allTransactionsAfterCommit.get(0)), is(equalTo(removeTransactionId(expectedPlannedTransactionAfterCommit))));

  }

  private Transaction removeTransactionId(Transaction transaction) {
    return Transaction.builder()
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .description(transaction.getDescription())
        .date(transaction.getDate())
        .categoryId(transaction.getCategoryId())
        .userId(transaction.getUserId())
        .isPlanned(transaction.isPlanned())
        .isRecurrent(transaction.isRecurrent())
        .build();
  }
}
