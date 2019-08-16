package com.pfm.planned.transaction;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class PlannedTransactionControllerIntegrationTest extends IntegrationTestsBase {

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
    PlannedTransaction expectedPlannedTransaction =
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

    PlannedTransaction plannedTransactionToAdd = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(plannedTransactionToAdd, jacekAccountId, foodCategoryId, token);

    PlannedTransaction addedPlannedTransaction = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    setPlannedTransactionIdAccountIdCategoryId(addedPlannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);

    //when
    callRestToDeletePlannedTransactionById(plannedTransactionId, token);

    //then
    List<PlannedTransaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    assertThat(allPlannedTransactionsInDb.size(), is(0));
    assertThat(allPlannedTransactionsInDb.contains(addedPlannedTransaction), is(false));

    BigDecimal jacekAccountBalanceAfterDeletingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterDeletingTransaction,
        is(accountJacekBalance1000().getBalance()));
  }

  //fixme add testing projected balance updates after deleting planned transaction
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
    List<PlannedTransaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    //then
    PlannedTransaction foodPlannedTransactionExpected =
        setPlannedTransactionIdAccountIdCategoryId(convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()),
            foodPlannedTransactionId, jacekAccountId, foodCategoryId);

    PlannedTransaction carPlannedTransactionExpected =
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
    List<PlannedTransaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);

    PlannedTransaction expected = convertPlannedTransactionRequestToPlannedTransactionAndSetId(foodPlannedTransactionId,
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

    long foodPlannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);
    long carPlannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
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

    //    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    //    callRestServiceToAddAccountAndReturnId(account, token);
    PlannedTransaction invalidPlannedTransaction = PlannedTransaction.builder()
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

}
