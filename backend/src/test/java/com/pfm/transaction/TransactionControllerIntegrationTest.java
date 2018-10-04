package com.pfm.transaction;

import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_CATEGORY;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_DATE;
import static com.pfm.config.MessagesProvider.EMPTY_TRANSACTION_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
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
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;

public class TransactionControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddTransaction() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());
    BigDecimal expectedPrice = accountJacekBalance1000().getBalance()
        .add(foodTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice());

    //when
    long transactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId);

    //then
    Transaction expectedTransaction =
        setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), transactionId, jacekAccountId, foodCategoryId);

    assertThat(callRestToGetTransactionById(transactionId), is(equalTo(expectedTransaction)));
    BigDecimal jacekAccountBalanceAfterAddingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId);

    assertThat(jacekAccountBalanceAfterAddingTransaction, is(expectedPrice));
  }

  @Test
  public void shouldDeleteTransaction() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());

    Transaction transactionToAdd = foodTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(transactionToAdd, jacekAccountId, foodCategoryId);

    Transaction addedTransaction = foodTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(addedTransaction, transactionId, jacekAccountId, foodCategoryId);

    //when
    deleteTransactionById(transactionId);

    //then
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase();

    assertThat(allTransactionsInDb.size(), is(0));
    assertThat(allTransactionsInDb.contains(addedTransaction), is(false));

    BigDecimal jacekAccountBalanceAfterDeletingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId);
    assertThat(jacekAccountBalanceAfterDeletingTransaction,
        is(accountJacekBalance1000().getBalance()));
  }

  @Test
  public void shouldGetTransactions() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar());
    long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId,
        foodCategoryId);
    long carTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), jacekAccountId, carCategoryId);

    //when
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase();

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
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long mbankAccountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar());
    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId);

    TransactionRequest updatedFoodTransactionRequest = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updatedFoodTransactionRequest.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodTransactionRequest.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodTransactionRequest.setCategoryId(carCategoryId);
    updatedFoodTransactionRequest.setDate(updatedFoodTransactionRequest.getDate().plusDays(1));
    updatedFoodTransactionRequest.setDescription("Car parts");

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + foodTransactionId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedFoodTransactionRequest)))
        .andExpect(status()
            .isOk());

    //then
    List<Transaction> allTransactionsInDb = callRestToGetAllTransactionsFromDatabase();
    assertThat(allTransactionsInDb.size(), is(1));
    assertTrue(allTransactionsInDb.contains(convertTransactionRequestToTransactionAndSetId(foodTransactionId, updatedFoodTransactionRequest)));

    BigDecimal jacekAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(jacekAccountId);
    assertThat(jacekAccountBalanceAfterTransactionUpdate, is(accountJacekBalance1000().getBalance()));

    BigDecimal piotrAccountBalanceAfterTransactionUpdate = callRestServiceAndReturnAccountBalance(mbankAccountId);
    assertThat(piotrAccountBalanceAfterTransactionUpdate,
        is(accountMbankBalance10().getBalance().add(updatedFoodTransactionRequest.getAccountPriceEntries().get(0).getPrice())));
  }

  @Test
  public void shouldReturnValidationErrorInUpdateMethod() throws Exception {

    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());

    final long foodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), jacekAccountId, foodCategoryId);
    TransactionRequest updateFoodTransaction = convertTransactionToTransactionRequest(foodTransactionWithNoAccountAndNoCategory());
    updateFoodTransaction.setCategoryId(foodCategoryId);
    updateFoodTransaction.getAccountPriceEntries().get(0).setAccountId(jacekAccountId);
    updateFoodTransaction.setDate(null);

    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + foodTransactionId)
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
        .contentType(JSON_CONTENT_TYPE)
        .content(json(transactionToAdd)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(EMPTY_TRANSACTION_NAME))))
        .andExpect(jsonPath("$[1]", Matchers.is(getMessage(EMPTY_TRANSACTION_CATEGORY))))
        .andExpect(jsonPath("$[2]", Matchers.is(getMessage(AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED))))
        .andExpect(jsonPath("$[3]", Matchers.is(getMessage(EMPTY_TRANSACTION_DATE))));
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
    mockMvc.perform(get(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInDeleteMethod() throws Exception {

    //when
    mockMvc.perform(delete(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingTransactionIdInUpdateMethod() throws Exception {

    //when
    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .content(json(foodTransactionWithNoAccountAndNoCategory()))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }
}