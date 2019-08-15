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

import com.pfm.account.Account;
import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    PlannedTransaction updatedFoodPlannedTransaction = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    updatedFoodPlannedTransaction.getAccountPriceEntries().get(0).setAccountId(mbankAccountId);
    updatedFoodPlannedTransaction.getAccountPriceEntries().get(0).setPrice(convertDoubleToBigDecimal(25));
    updatedFoodPlannedTransaction.setCategoryId(carCategoryId);
    updatedFoodPlannedTransaction.setDate(updatedFoodPlannedTransaction.getDate().plusDays(1));
    updatedFoodPlannedTransaction.setDescription("Car parts");

    //when
    callRestToUpdatePlannedTransaction(foodPlannedTransactionId, updatedFoodPlannedTransaction, token);
    //fixme lukasz  below instead accounts balance update  -  projected accounts balance should be taken care of !!!
    //then
    List<PlannedTransaction> allPlannedTransactionsInDb = callRestToGetAllPlannedTransactionsFromDatabase(token);
    assertThat(allPlannedTransactionsInDb.size(), is(1));

    //    PlannedTransaction expected = convertPlannedTransactionRequestToPlannedTransactionAndSetId(foodPlannedTransactionId,
    //        updatedFoodPlannedTransactionRequest);

    //    assertThat(allPlannedTransactionsInDb, contains(expected));

    BigDecimal jacekAccountBalanceAfterPlannedTransactionUpdate = callRestServiceAndReturnAccountBalance(jacekAccountId, token);
    assertThat(jacekAccountBalanceAfterPlannedTransactionUpdate, is(accountJacekBalance1000().getBalance()));

    //    BigDecimal piotrAccountBalanceAfterPlannedTransactionUpdate = callRestServiceAndReturnAccountBalance(mbankAccountId, token);
    //    assertThat(piotrAccountBalanceAfterPlannedTransactionUpdate,
    //        is(accountMbankBalance10().getBalance().add(updatedFoodPlannedTransactionRequest.getAccountPriceEntries().get(0).getPrice())));
  }

  @Test
  public void shouldUpdateProjectedAccountBalanceDuringAddingPlannedTransaction() throws Exception {
    //fixme lukasz may be simplified
    //given
    Account jacekAccount = accountJacekBalance1000();
    jacekAccount.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(jacekAccount, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    //when
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(
        convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);

    //then
    setPlannedTransactionIdAccountIdCategoryId(convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()),
        plannedTransactionId, jacekAccountId, foodCategoryId);
    List<Account> userJacekAccounts = callRestToGetAllAccounts(token);
    Optional<Account> updatedProjectedAccountBalance = userJacekAccounts.stream()
        .filter(acc -> acc.getId() == jacekAccountId)
        .findFirst();

    final BigDecimal projectedBalance = updatedProjectedAccountBalance.get().getProjectedBalance();
    final BigDecimal balance = updatedProjectedAccountBalance.get().getBalance();

    assertThat(projectedBalance, is(convertDoubleToBigDecimal(990d)));
  }

  @Test
  public void shouldUpdateProjectedAccountBalanceDuringUpdatingPlannedTransaction() throws Exception {
    //fixme lukasz may be simplified  - maybe Jsonpath asserts after update ?
    //given
    Account jacekAccount = accountJacekBalance1000();
    jacekAccount.setCurrency(currencyService.getCurrencies(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(jacekAccount, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    PlannedTransaction plannedTransaction = convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory());
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(plannedTransaction, jacekAccountId, foodCategoryId, token);

    setPlannedTransactionIdAccountIdCategoryId(plannedTransaction, plannedTransactionId, jacekAccountId, foodCategoryId);
    List<Account> accounts = callRestToGetAllAccounts(token);
    Optional<Account> updatedProjectedBalance = accounts.stream()
        .filter(account -> account.getId() == jacekAccountId)
        .findFirst();

    final BigDecimal projectedBalance = updatedProjectedBalance.get().getProjectedBalance();
    final BigDecimal balance = updatedProjectedBalance.get().getBalance();

    assertThat(projectedBalance, is(convertDoubleToBigDecimal(990d)));

    //when
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    PlannedTransaction updatedPlannedTransaction = convertTransactionToPlannedTransaction(carTransactionWithNoAccountAndNoCategory());
    setPlannedTransactionIdAccountIdCategoryId(updatedPlannedTransaction, plannedTransactionId, jacekAccountId, carCategoryId);

    callRestToUpdatePlannedTransaction(plannedTransactionId, updatedPlannedTransaction, token);

    //then
    var accountsAfterUpdate = callRestToGetAllAccounts(token);
    Optional<Account> updatedProjectedBalance2 = accountsAfterUpdate.stream()
        .filter(account -> account.getId() == jacekAccountId)
        .findFirst();
//    BigDecimal babab = updatedProjectedBalance2.ifPresent(account -> {
//      return account.getProjectedBalance();
//
//
//
//      );

    final BigDecimal projectedBalance2 = updatedProjectedBalance2.get().getProjectedBalance();
    final BigDecimal balance2 = updatedProjectedBalance2.get().getBalance();

    assertThat(projectedBalance2, is(convertDoubleToBigDecimal(970d)));

  }

}
