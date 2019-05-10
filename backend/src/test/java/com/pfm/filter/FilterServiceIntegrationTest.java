package com.pfm.filter;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestFilterProvider.filterHomeExpensesUpTo200;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.helpers.IntegrationTestsBase;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FilterServiceIntegrationTest extends IntegrationTestsBase {

  @Autowired
  private AccountService accountService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private FilterService filterService;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void before() {
    super.before();
    userId = userService.registerUser(userZdzislaw()).getId();
  }

  @Test
  public void shouldCheckIfFilterExistByAccountId() {
    //given
    currencyService.addDefaultCurrencies(userId);

    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(2));

    long accountId = accountService.saveAccount(userId, account).getId();

    Filter filter = filterFoodExpenses();
    filter.setAccountIds(Collections.singletonList(accountId));
    long filterId = filterService.addFilter(userId, filter).getId();

    //when
    assertTrue(filterService.filterExistByAccountId(accountId));

    filterService.deleteFilter(filterId);

    assertFalse(filterService.filterExistByAccountId(accountId));
  }

  @Test
  public void shouldCheckIfFilterExistByCategoryId() {
    //given
    Category category = categoryCar();
    Long categoryId = categoryService.addCategory(category, userId).getId();

    Filter filter = filterHomeExpensesUpTo200();
    filter.setCategoryIds(Collections.singletonList(categoryId));
    long filterId = filterService.addFilter(userId, filter).getId();

    //when
    assertTrue(filterService.filterExistByCategoryId(categoryId));

    filterService.deleteFilter(filterId);

    assertFalse(filterService.filterExistByCategoryId(categoryId));
  }

}
