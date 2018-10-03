package com.pfm.auth;

import static com.pfm.helpers.TestAccountProvider.accountIdeaBalance100000;
import static com.pfm.helpers.TestAccountProvider.accountIngBalance9999;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestAccountProvider.accountMilleniumBalance100;
import static com.pfm.helpers.TestCategoryProvider.categoryAnimals;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestTransactionProvider.animalsTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.homeTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.transaction.Transaction;
import java.util.List;
import org.junit.Test;

public class MultipleUserIntegrationTests extends IntegrationTestsBase {

  @Test
  public void shouldRegisterTwoUsersAndAddAccountsCatgoriesTransaction() throws Exception {

    //given
    long userMarianId = callRestToRegisterUserAndReturnUserId(userMarian());
    String marianToken = callRestToAuthenticateUserAndReturnToken(userMarian());
    long userZdzislawId = callRestToRegisterUserAndReturnUserId(userZdzislaw());
    String zdzislawToken = callRestToAuthenticateUserAndReturnToken(userZdzislaw());

    //when
    long marianAccountMbankId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), marianToken);
    long marianAccountMilleniumId = callRestServiceToAddAccountAndReturnId(accountMilleniumBalance100(), marianToken);
    long marianCategoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), marianToken);
    long marianCategoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), marianToken);
    long marianFoodTransactionId = callRestToAddTransactionAndReturnId(foodTransactionWithNoAccountAndNoCategory(), marianAccountMbankId,
        marianCategoryFoodId, marianToken);
    long marianCarTransactionId = callRestToAddTransactionAndReturnId(carTransactionWithNoAccountAndNoCategory(), marianAccountMilleniumId,
        marianCategoryCarId, marianToken);

    long zdzislawAccountIngId = callRestServiceToAddAccountAndReturnId(accountIngBalance9999(), zdzislawToken);
    long zdzislawAccountIdeaId = callRestServiceToAddAccountAndReturnId(accountIdeaBalance100000(), zdzislawToken);
    long zdzislawCategoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), zdzislawToken);
    long zdzislawCategoryAnimalsId = callRestToAddCategoryAndReturnId(categoryAnimals(), zdzislawToken);
    long zdzislawTransactionAnimalsId = callRestToAddTransactionAndReturnId(animalsTransactionWithNoAccountAndNoCategory(), zdzislawAccountIngId,
        zdzislawCategoryAnimalsId, zdzislawToken);
    long zdzislawTransactionHomeId = callRestToAddTransactionAndReturnId(homeTransactionWithNoAccountAndNoCategory(), zdzislawAccountIdeaId,
        zdzislawCategoryHomeId, zdzislawToken);

    //then
    List<Account> accountsMarian = callRestToGetAllAccounts(marianToken);

    Account marianAccountMbankExpected = accountMbankBalance10();
    marianAccountMbankExpected.setId(marianAccountMbankId);
    marianAccountMbankExpected.setBalance(accountMbankBalance10().getBalance().add(foodTransactionWithNoAccountAndNoCategory().getPrice()));
    marianAccountMbankExpected.setUserId(userMarianId);

    Account marianAccountMilleniumExpected = accountMilleniumBalance100();
    marianAccountMilleniumExpected.setId(marianAccountMilleniumId);
    marianAccountMilleniumExpected
        .setBalance(accountMilleniumBalance100().getBalance().add(carTransactionWithNoAccountAndNoCategory().getPrice()));
    marianAccountMilleniumExpected.setUserId(userMarianId);

    assertThat(accountsMarian, hasSize(2));
    assertThat(accountsMarian, containsInAnyOrder(marianAccountMbankExpected, marianAccountMilleniumExpected));

    List<Account> accountsZdzislaw = callRestToGetAllAccounts(zdzislawToken);

    Account zdzislawAccountIngExpected = accountIngBalance9999();
    zdzislawAccountIngExpected.setId(zdzislawAccountIngId);
    zdzislawAccountIngExpected.setBalance(accountIngBalance9999().getBalance().add(animalsTransactionWithNoAccountAndNoCategory().getPrice()));
    zdzislawAccountIngExpected.setUserId(userZdzislawId);

    Account zdzislawAccountIdeaExpected = accountIdeaBalance100000();
    zdzislawAccountIdeaExpected.setId(zdzislawAccountIdeaId);
    zdzislawAccountIdeaExpected.setBalance(accountIdeaBalance100000().getBalance().add(homeTransactionWithNoAccountAndNoCategory().getPrice()));
    zdzislawAccountIdeaExpected.setUserId(userZdzislawId);

    assertThat(accountsZdzislaw, hasSize(2));
    assertThat(accountsZdzislaw, containsInAnyOrder(zdzislawAccountIngExpected, zdzislawAccountIdeaExpected));

    List<Category> marianCategories = callRestToGetAllCategories(marianToken);

    Category marianCategoryCarExpected = categoryCar();
    marianCategoryCarExpected.setId(marianCategoryCarId);
    marianCategoryCarExpected.setUserId(userMarianId);

    Category marianCategoryFoodExpected = categoryFood();
    marianCategoryFoodExpected.setId(marianCategoryFoodId);
    marianCategoryFoodExpected.setUserId(userMarianId);

    assertThat(marianCategories, hasSize(2));
    assertThat(marianCategories, containsInAnyOrder(marianCategoryCarExpected, marianCategoryFoodExpected));

    List<Category> zdzislawCategories = callRestToGetAllCategories(zdzislawToken);

    Category zdzislawCategoryHomeExpected = categoryHome();
    zdzislawCategoryHomeExpected.setId(zdzislawCategoryHomeId);
    zdzislawCategoryHomeExpected.setUserId(userZdzislawId);

    Category zdzislawCategoryAnimalsExpected = categoryAnimals();
    zdzislawCategoryAnimalsExpected.setId(zdzislawCategoryAnimalsId);
    zdzislawCategoryAnimalsExpected.setUserId(userZdzislawId);

    assertThat(zdzislawCategories, hasSize(2));
    assertThat(zdzislawCategories, containsInAnyOrder(zdzislawCategoryAnimalsExpected, zdzislawCategoryHomeExpected));

    List<Transaction> marianTransactions = callRestToGetAllTransactionsFromDatabase(marianToken);

    Transaction marianFoodTransactionExpected = foodTransactionWithNoAccountAndNoCategory();
    marianFoodTransactionExpected.setId(marianFoodTransactionId);
    marianFoodTransactionExpected.setAccountId(marianAccountMbankId);
    marianFoodTransactionExpected.setCategoryId(marianCategoryFoodId);
    marianFoodTransactionExpected.setUserId(userMarianId);

    Transaction marianCarTransactionExpected = carTransactionWithNoAccountAndNoCategory();
    marianCarTransactionExpected.setId(marianCarTransactionId);
    marianCarTransactionExpected.setAccountId(marianAccountMilleniumId);
    marianCarTransactionExpected.setCategoryId(marianCategoryCarId);
    marianCarTransactionExpected.setUserId(userMarianId);

    assertThat(marianTransactions, hasSize(2));
    assertThat(marianTransactions, containsInAnyOrder(marianFoodTransactionExpected, marianCarTransactionExpected));

    List<Transaction> zdzislawTransactions = callRestToGetAllTransactionsFromDatabase(zdzislawToken);

    Transaction zdzislawAnimalsTransactionsExpected = animalsTransactionWithNoAccountAndNoCategory();
    zdzislawAnimalsTransactionsExpected.setId(zdzislawTransactionAnimalsId);
    zdzislawAnimalsTransactionsExpected.setAccountId(zdzislawAccountIngId);
    zdzislawAnimalsTransactionsExpected.setCategoryId(zdzislawCategoryAnimalsId);
    zdzislawAnimalsTransactionsExpected.setUserId(userZdzislawId);

    Transaction zdzislawHomeTransactionsExpected = homeTransactionWithNoAccountAndNoCategory();
    zdzislawHomeTransactionsExpected.setId(zdzislawTransactionHomeId);
    zdzislawHomeTransactionsExpected.setAccountId(zdzislawAccountIdeaId);
    zdzislawHomeTransactionsExpected.setCategoryId(zdzislawCategoryHomeId);
    zdzislawHomeTransactionsExpected.setUserId(userZdzislawId);

    assertThat(zdzislawTransactions, hasSize(2));
    assertThat(zdzislawTransactions, containsInAnyOrder(zdzislawAnimalsTransactionsExpected, zdzislawHomeTransactionsExpected));

  }

  @Test
  public void shouldReturnUnauthorizedCausedByWrongToken() throws Exception {

    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header("Authorization", "Wrong token")
            .content(json(convertAccountToAccountRequest(accountJacekBalance1000())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedCausedByEmptyToken() throws Exception {

    //given
    mockMvc
        .perform(post(ACCOUNTS_SERVICE_PATH)
            .header("Authorization", "")
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

}
