package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_ACCOUNT;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_PRICE;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static junit.framework.TestCase.assertTrue;
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

import com.pfm.IntegrationTestsBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class TransactionControllerIntegrationTest extends IntegrationTestsBase {

  private String token;
  private long userId;

  @Before
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = autheticateUserAndReturnUserToken(userMarian());
  }

  @Test
  public void shouldAddTransaction() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    //when
    long transactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId, token);

    //then
    Transaction expectedTransaction =
        setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), transactionId, jacekAccountId, foodCategoryId);
    expectedTransaction.setUserId(userId);

    assertThat(callRestToGetTransactionById(transactionId, token), is(equalTo(expectedTransaction)));
    BigDecimal jacekAccountBalanceAfterAddingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterAddingTransaction,
        is(accountJacekBalance1000().getBalance().add(foodTransactionWithNoAccountAndNoCategory().getPrice())));
  }

  @Test
  public void shouldDeleteTransaction() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Transaction transactionToAdd = foodTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(transactionToAdd, jacekAccountId, foodCategoryId, token);

    Transaction addedTransaction = foodTransactionWithNoAccountAndNoCategory();
    addedTransaction.setId(transactionId);
    addedTransaction.setAccountId(jacekAccountId);
    addedTransaction.setCategoryId(foodCategoryId);

    //when
    deleteTransactionById(transactionId, token);

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
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId,
        foodCategoryId, token);
    long carTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), jacekAccountId, carCategoryId, token);

    //when
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase(token);

    //then
    Transaction foodTransactionExpected =
        setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), foodTransactionId, jacekAccountId, foodCategoryId);
    foodTransactionExpected.setUserId(userId);

    Transaction carTransactionExpected =
        setTransactionIdAccountIdCategoryId(carTransactionWithNoAccountAndNoCategory(), carTransactionId, jacekAccountId, carCategoryId);
    carTransactionExpected.setUserId(userId);

    assertThat(allTransactionsInDb.size(), is(2));
    assertThat(allTransactionsInDb, containsInAnyOrder(foodTransactionExpected, carTransactionExpected));

  }

  @Test
  public void shouldUpdateTransaction() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);

    TransactionRequest updatedFoodTransactionRequest = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updatedFoodTransactionRequest.setAccountId(mbankAccountId);
    updatedFoodTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodTransactionRequest.setDate(updatedFoodTransactionRequest.getDate().plusDays(1));
    updatedFoodTransactionRequest.setPrice(convertDoubleToBigDecimal(25));
    updatedFoodTransactionRequest.setDescription("Car parts");

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + foodTransactionId)
        .header("Authorization", token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedFoodTransactionRequest)))
        .andExpect(status()
            .isOk());

    //then
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(allTransactionsInDb.size(), is(1));

    Transaction expected = convertTransactionRequestToTransactionAndSetId(foodTransactionId, updatedFoodTransactionRequest);
    expected.setUserId(userId);

    assertTrue(allTransactionsInDb.contains(expected));

    BigDecimal jacekAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterTransactionUpdate, is(accountJacekBalance1000().getBalance()));

    BigDecimal piotrAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(mbankAccountId, token);
    assertThat(piotrAccountBalanceAfterTransactionUpdate,
        is(accountMbankBalance10().getBalance().add(updatedFoodTransactionRequest.getPrice())));
  }

  @Test
  public void shouldReturnValidationErrorInUpdateMethod() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId,
        token);
    TransactionRequest updateFoodTransaction = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updateFoodTransaction.setCategoryId(foodCategoryId);
    updateFoodTransaction.setAccountId(jacekAccountId);
    updateFoodTransaction.setDate(null);

    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + foodTransactionId)
        .header("Authorization", token)
        .content(json(updateFoodTransaction))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_DATE))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyFields() throws Exception {

    //given
    TransactionRequest transactionToAdd = new TransactionRequest();

    //when
    mockMvc.perform(post(TRANSACTIONS_SERVICE_PATH)
        .header("Authorization", token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_NAME))))
        .andExpect(jsonPath("$[1]", Matchers.is(getMessage(EMPTY_TRANSACTION_CATEGORY))))
        .andExpect(jsonPath("$[2]", Matchers.is(getMessage(EMPTY_TRANSACTION_ACCOUNT))))
        .andExpect(jsonPath("$[3]", Matchers.is(getMessage(EMPTY_TRANSACTION_DATE))))
        .andExpect(jsonPath("$[4]", Matchers.is(getMessage(EMPTY_TRANSACTION_PRICE))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyDescription() throws Exception {

    //given
    TransactionRequest transactionToAdd = TransactionRequest.builder()
        .description(" ")
        .accountId(NOT_EXISTING_ID)
        .categoryId(NOT_EXISTING_ID)
        .date(LocalDate.of(2018, 10, 10))
        .price(convertDoubleToBigDecimal(10))
        .build();

    //when
    mockMvc.perform(post(TRANSACTIONS_SERVICE_PATH)
        .header("Authorization", token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_NAME))))
        .andExpect(jsonPath("$[1]", Matchers.is(getMessage(CATEGORY_ID_DOES_NOT_EXIST))))
        .andExpect(jsonPath("$[2]", Matchers.is(getMessage(ACCOUNT_ID_DOES_NOT_EXIST))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInGetMethod() throws Exception {

    //when
    mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInDeleteMethod() throws Exception {

    //when
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInUpdateMethod() throws Exception {

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header("Authorization", token)
        .content(json(foodTransactionWithNoAccountAndNoCategory()))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }
}