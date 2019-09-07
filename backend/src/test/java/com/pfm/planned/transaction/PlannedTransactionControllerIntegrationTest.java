package com.pfm.planned.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_IS_ARCHIVED;
import static com.pfm.config.MessagesProvider.FUTURE_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.PAST_PLANNED_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.getMessage;
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
import static com.pfm.helpers.TransactionHelper.convertTransactionToTransactionRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.pfm.transaction.TransactionRequest;
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

    TransactionRequest updatedPlannedTransactionRequest = convertTransactionToTransactionRequest(updatedPlannedTransaction);
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
    TransactionRequest plannedTransactionRequest = convertTransactionToTransactionRequest(plannedTransaction);
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

    TransactionRequest updatedFoodPlannedTransactionRequest = convertTransactionToTransactionRequest(
        foodPlannedTransactionWithNoAccountAndNoCategory());
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodPlannedTransactionRequest.setDate(updatedFoodPlannedTransactionRequest.getDate().plusDays(1));
    updatedFoodPlannedTransactionRequest.setDescription("Car parts");

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_PLANNED_TRANSACTION_ID)
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

    TransactionRequest updatedFoodPlannedTransactionRequest = convertTransactionToTransactionRequest(
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
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_PLANNED_TRANSACTION_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldCommitPlannedTransaction() throws Exception {
    //given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Transaction plannedTransaction = foodPlannedTransactionWithNoAccountAndNoCategory();
    plannedTransaction.setPlanned(true);

    long plannedTransactionId = callRestToAddTransactionAndReturnId(plannedTransaction, jacekAccountId, foodCategoryId, token);

    Transaction expectedPlannedTransaction = setTransactionIdAccountIdCategoryId(foodPlannedTransactionWithNoAccountAndNoCategory(),
        plannedTransactionId, jacekAccountId, foodCategoryId);

    List<Transaction> allTransactions = callRestToGetAllTransactionsFromDatabase(token);
    List<Transaction> allPlannedTransactions = callRestToGetAllPlannedTransactionsFromDatabase(token);

    assertThat(allTransactions.size(), is(0));
    assertThat(allPlannedTransactions.size(), is(1));
    assertThat(allPlannedTransactions.get(0), is(equalTo(expectedPlannedTransaction)));

    mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + plannedTransactionId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    final List<Transaction> allTransactionsAfterCommit = callRestToGetAllTransactionsFromDatabase(token);
    final List<Transaction> allPlannedTransactionsAfterCommit = callRestToGetAllPlannedTransactionsFromDatabase(token);

    expectedPlannedTransaction.setDate(LocalDate.now());
    expectedPlannedTransaction.setPlanned(false);

    assertThat(allTransactionsAfterCommit.size(), is(1));
    assertThat(allPlannedTransactionsAfterCommit.size(), is(0));
    assertThat(removeTransactionId(allTransactionsAfterCommit.get(0)), is(equalTo(removeTransactionId(expectedPlannedTransaction))));

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

    TransactionRequest transactionRequest = convertTransactionToTransactionRequest(transaction);

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

    TransactionRequest transactionRequest = convertTransactionToTransactionRequest(transaction);

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
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(PAST_PLANNED_TRANSACTION_DATE))));

  }

  @Test
  public void shouldReturnNotFoundForNotDuringCommittingNotExistingPlannedTransaction() throws Exception {
    //given
    long notExistingTransactionId = -2L;
    mockMvc
        .perform(patch(TRANSACTIONS_SERVICE_PATH + "/" + notExistingTransactionId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());

  }

  private Transaction removeTransactionId(Transaction transaction) {
    return Transaction.builder()
        .accountPriceEntries(transaction.getAccountPriceEntries())
        .description(transaction.getDescription())
        .date(transaction.getDate())
        .categoryId(transaction.getCategoryId())
        .userId(transaction.getUserId())
        .isPlanned(transaction.isPlanned())
        .build();
  }
}
