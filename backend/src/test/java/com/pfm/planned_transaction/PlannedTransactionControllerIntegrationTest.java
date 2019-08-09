package com.pfm.planned_transaction;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestTransactionProvider.convertTransactionToPlannedTransaction;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.account.Account;
import com.pfm.helpers.IntegrationTestsBase;
import java.math.BigDecimal;
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
    BigDecimal expectedPrice = accountJacekBalance1000().getBalance()
        .add(foodTransactionWithNoAccountAndNoCategory().getAccountPriceEntries().get(0).getPrice());

    //when
    long plannedTransactionId = callRestToAddPlannedTransactionAndReturnId(convertTransactionToPlannedTransaction(foodTransactionWithNoAccountAndNoCategory()), jacekAccountId, foodCategoryId, token);

    //then
    PlannedTransaction expectedPlannedTransaction =
        (PlannedTransaction) setTransactionIdAccountIdCategoryId(foodTransactionWithNoAccountAndNoCategory(), plannedTransactionId, jacekAccountId, foodCategoryId);

    assertThat(callRestToGetTransactionById(plannedTransactionId, token), is(equalTo(expectedPlannedTransaction)));
    BigDecimal jacekAccountBalanceAfterAddingTransaction = callRestServiceAndReturnAccountBalance(jacekAccountId, token);

    assertThat(jacekAccountBalanceAfterAddingTransaction, is(expectedPrice));
  }

}
