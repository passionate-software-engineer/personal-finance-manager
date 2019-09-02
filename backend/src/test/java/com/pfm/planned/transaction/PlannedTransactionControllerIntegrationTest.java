package com.pfm.planned.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_IS_ARCHIVED;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.convertTransactionToPlannedTransaction;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.config.MessagesProvider.Language;
import com.pfm.currency.Currency;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class PlannedTransactionControllerIntegrationTest extends IntegrationTestsBase {

  private static final long NOT_EXISTING_PLANNED_TRANSACTION_ID = 7389387L;
  private static final String EMPTY_DESCRIPTION = "";

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddPlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    //when
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);

    //then
    Transaction expectedPlannedTransaction =
        setPlannedTransactionIdAccountIdCategoryId(convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()),
            plannedTransactionId, jacekAccountId, foodCategoryId);

    assertThat(callRestToGetPlannedTransactionById(plannedTransactionId, token), is(equalTo(expectedPlannedTransaction)));

  }

  @Test
  public void shouldDeletePlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Transaction plannedTransactionToAdd = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(plannedTransactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedPlannedTransaction = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    setPlannedTransactionIdAccountIdCategoryId(addedPlannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);

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
    long foodPlannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);
    long carPlannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(carTransactionWithNoAccountAndNoCategory()), jacekAccountId, carCategoryId, token);

    //when
    List<Transaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    //then
    Transaction foodPlannedTransactionExpected =
        setPlannedTransactionIdAccountIdCategoryId(convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()),
            foodPlannedTransactionId, jacekAccountId, foodCategoryId);

    Transaction carPlannedTransactionExpected =
        setPlannedTransactionIdAccountIdCategoryId(convertTransactionToPlannedTransaction(carTransactionWithNoAccountAndNoCategory()),
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
    final long foodPlannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId,
        token);

    PlannedTransactionRequest updatedFoodPlannedTransactionRequest = convertPlannedTransactionToPlannedTransactionRequest(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()));
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription("Car parts");

    //when
    callRestToUpdatePlannedTransaction(foodPlannedTransactionId, updatedFoodPlannedTransactionRequest, token);
    List<Transaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    Transaction expected = convertPlannedTransactionRequestToPlannedTransactionAndSetId(foodPlannedTransactionId,
        updatedFoodPlannedTransactionRequest);

    //then
    assertThat(allPlannedTransactionsInDb.size(), is(1));
    assertThat(allPlannedTransactionsInDb.get(0), equalTo(expected));

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

    callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);
    callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(carTransactionWithNoAccountAndNoCategory()), jacekAccountId, carCategoryId, token);

    mockMvc
        .perform(get(PLANNED_TRANSACTIONS_SERVICE_PATH + "/" + notExistingPlannedTransactionId)
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
            post(PLANNED_TRANSACTIONS_SERVICE_PATH)
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
    PlannedTransactionRequest plannedTransactionRequest = convertPlannedTransactionToPlannedTransactionRequest(plannedTransaction);
    mockMvc
        .perform(post(PLANNED_TRANSACTIONS_SERVICE_PATH)
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
    callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId,
        token);

    PlannedTransactionRequest updatedFoodPlannedTransactionRequest = convertPlannedTransactionToPlannedTransactionRequest(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()));
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription("Car parts");

    //when
    mockMvc.perform(put(PLANNED_TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_PLANNED_TRANSACTION_ID)
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
    final long transactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId,
        token);

    PlannedTransactionRequest updatedFoodPlannedTransactionRequest = convertPlannedTransactionToPlannedTransactionRequest(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()));
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription(EMPTY_DESCRIPTION);

    //when
    mockMvc.perform(put(PLANNED_TRANSACTIONS_SERVICE_PATH + "/" + transactionId)
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

    Transaction plannedTransactionToAdd = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(plannedTransactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedPlannedTransaction = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    setPlannedTransactionIdAccountIdCategoryId(addedPlannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);

    //when
    mockMvc.perform(delete(PLANNED_TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_PLANNED_TRANSACTION_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }
}
