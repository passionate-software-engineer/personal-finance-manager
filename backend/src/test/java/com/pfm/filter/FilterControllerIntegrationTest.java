package com.pfm.filter;

import static com.pfm.config.MessagesProvider.ACCOUNT_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestFilterProvider.convertIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterCarExpenses;
import static com.pfm.helpers.TestFilterProvider.filterCarExpensesWithoutSettingDefault;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestFilterProvider.filterHomeExpensesUpTo200;
import static com.pfm.helpers.TestFilterProvider.filterIsDefault;
import static com.pfm.helpers.TestFilterProvider.filterIsNotDefault;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.helpers.IntegrationTestsBase;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;

public class FilterControllerIntegrationTest extends IntegrationTestsBase {

  @BeforeEach
  public void beforeEach() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @SuppressWarnings("unused")
  private static Collection<Object[]> addFilterParameters() {
    return Arrays.asList(new Object[][]{
        {filterHomeExpensesUpTo200()},
        {filterIsDefault()},
        {filterIsNotDefault()}
    });
  }

  @ParameterizedTest
  @MethodSource("addFilterParameters")
  public void shouldAddFilter(Filter filter) throws Exception {
    // given
    Long categoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(2));
    account.setType(accountTypeService.getAccountTypes(userId).get(2));

    Long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(filter);
    homeExpensesFilterToAdd.setCategoryIds(convertIdsToList(categoryId));
    homeExpensesFilterToAdd.setAccountIds(convertIdsToList(accountId));

    // when
    Long filterId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd, token);

    // then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, homeExpensesFilterToAdd);

    Filter actualFilter = getFilterById(filterId, token);
    assertThat(actualFilter, is(equalTo(expectedFilter)));
    assertThat(actualFilter.getIsDefault(), is(expectedFilter.getIsDefault()));
  }

  @Test
  public void shouldGetFilterById() throws Exception {
    // given

    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(2));
    account.setType(accountTypeService.getAccountTypes(userId).get(2));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertIdsToList(accountId));
    carExpensesFilterToAdd.setCategoryIds(convertIdsToList(categoryId));
    long filterId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd, token);

    // when
    Filter outputFilter = getFilterById(filterId, token);

    // then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, carExpensesFilterToAdd);
    assertThat(expectedFilter, is(equalTo(outputFilter)));
  }

  @Test
  public void shouldGetAllFilters() throws Exception {
    // given
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long categoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), token);

    Account accountJacek = accountJacekBalance1000();
    accountJacek.setCurrency(currencyService.getCurrencies(userId).get(2));
    accountJacek.setType(accountTypeService.getAccountTypes(userId).get(2));

    final long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacek, token);

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(2));
    accountMbank.setType(accountTypeService.getAccountTypes(userId).get(2));

    long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbank, token);

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(
        filterHomeExpensesUpTo200());
    homeExpensesFilterToAdd.setCategoryIds(convertIdsToList(categoryHomeId));
    final long filterHomeExpensesId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd,
        token);

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertIdsToList(accountMbankId, accountJacekId));
    carExpensesFilterToAdd.setCategoryIds(convertIdsToList(categoryCarId));
    final long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd,
        token);

    FilterRequest foodExpensesFilterToAdd = convertFilterToFilterRequest(filterFoodExpenses());
    foodExpensesFilterToAdd.setAccountIds(convertIdsToList(accountJacekId));
    foodExpensesFilterToAdd.setCategoryIds(convertIdsToList(categoryFoodId));
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(foodExpensesFilterToAdd,
        token);

    // when
    final List<Filter> actualListOfFilters = callRestToGetAllFilters(token);

    // then
    final Filter expectedCarExpensesFilter = convertFilterRequestToFilterAndSetId(
        filterCarExpensesId, carExpensesFilterToAdd);

    final Filter expectedFoodExpensesFilter = convertFilterRequestToFilterAndSetId(
        filterFoodExpensesId, foodExpensesFilterToAdd);

    final Filter expectedHomeExpensesFilter = convertFilterRequestToFilterAndSetId(
        filterHomeExpensesId, homeExpensesFilterToAdd);

    assertThat(actualListOfFilters.size(), is(3));
    assertThat(actualListOfFilters, containsInAnyOrder(
        expectedCarExpensesFilter,
        expectedFoodExpensesFilter,
        expectedHomeExpensesFilter
    ));
  }

  @Test
  public void shouldDeleteFilter() throws Exception {
    // given
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filterCarExpenses(), token);
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(filterFoodExpenses(), token);

    // when
    callRestToDeleteFilterById(filterCarExpensesId, token);

    // then
    List<Filter> actualFilters = callRestToGetAllFilters(token);

    assertThat(actualFilters.size(), is(1));

    Filter expectedFoodExpenses = filterFoodExpenses();
    expectedFoodExpenses.setId(filterFoodExpensesId);

    Filter expetedCarExpenses = filterCarExpenses();
    expetedCarExpenses.setId(filterCarExpensesId);

    assertThat(actualFilters, contains(expectedFoodExpenses));
    assertThat(actualFilters.contains(expetedCarExpenses), is(false));
  }

  @SuppressWarnings("unused")
  private static Collection<Object[]> updateFilterParameters() {
    return Arrays.asList(new Object[][]{
        {filterIsDefault().getIsDefault(), filterCarExpenses()},
        {filterIsNotDefault().getIsDefault(), filterCarExpensesWithoutSettingDefault()}
    });
  }

  @ParameterizedTest
  @MethodSource("updateFilterParameters")
  public void shouldUpdateFilter(Boolean defaultOrNoDefault, Filter filter) throws Exception {
    // given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(2));
    account.setType(accountTypeService.getAccountTypes(userId).get(2));

    long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filter, token);
    FilterRequest filterCarExpensesToUpdate = FilterRequest.builder()
        .name("Car expenses between 1000$ and 2000$")
        .priceTo(convertDoubleToBigDecimal(2000))
        .priceFrom(convertDoubleToBigDecimal(1000))
        .description("Car")
        .dateFrom(LocalDate.of(2017, 1, 1))
        .dateTo(LocalDate.of(2017, 1, 31))
        .categoryIds(convertIdsToList(categoryId))
        .accountIds(convertIdsToList(accountId))
        .isDefault(defaultOrNoDefault)
        .build();
    // when
    callRestServiceToUpdateFilter(filterCarExpensesId, filterCarExpensesToUpdate, token);
    // then
    Filter updatedFilter = getFilterById(filterCarExpensesId, token);
    final Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterCarExpensesId,
        filterCarExpensesToUpdate);
    assertThat(updatedFilter, is(equalTo(expectedFilter)));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInGetMethod() throws Exception {
    // when
    mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInDeleteMethod() throws Exception {
    // when
    mockMvc
        .perform(delete(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInUpdateMethod() throws Exception {
    // when
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(convertFilterToFilterRequest(filterCarExpenses())))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInAddMethod() throws Exception {
    // given
    FilterRequest filterRequestWithValidationErrors = FilterRequest.builder()
        .accountIds(convertIdsToList(1L))
        .categoryIds(convertIdsToList(1L))
        .dateFrom(LocalDate.of(2018, 1, 1))
        .dateTo(LocalDate.of(2017, 1, 1))
        .priceFrom(convertDoubleToBigDecimal(100))
        .priceTo(convertDoubleToBigDecimal(50))
        .build();

    // when
    mockMvc
        .perform(post(FILTERS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInAddMethodSecondCase() throws Exception {
    // given
    FilterRequest filterRequestWithValidationErrors = FilterRequest.builder()
        .dateFrom(LocalDate.of(2018, 1, 1))
        .priceFrom(convertDoubleToBigDecimal(100))
        .build();

    // when
    mockMvc
        .perform(post(FILTERS_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInUpdateMethod() throws Exception {
    // given
    final long filterId = callRestServiceToAddFilterAndReturnId(filterCarExpenses(), token);
    FilterRequest filterRequestWithValidationErrors = new FilterRequest();
    filterRequestWithValidationErrors.setName(" ");
    filterRequestWithValidationErrors.setAccountIds(convertIdsToList(1L));
    filterRequestWithValidationErrors.setCategoryIds(convertIdsToList(1L));
    filterRequestWithValidationErrors.setDateFrom(LocalDate.of(2018, 1, 1));
    filterRequestWithValidationErrors.setDateTo(LocalDate.of(2017, 1, 1));
    filterRequestWithValidationErrors.setPriceFrom(convertDoubleToBigDecimal(100));
    filterRequestWithValidationErrors.setPriceTo(convertDoubleToBigDecimal(50));
    filterRequestWithValidationErrors.setDescription("description");

    // when
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + filterId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorWhenTryingToDeleteAccountUsedInFilter() throws Exception {
    // given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);

    Filter filter = filterCarExpenses();
    filter.setAccountIds(convertIdsToList(jacekAccountId));
    callRestServiceToAddFilterAndReturnId(filter, token);

    mockMvc.perform(delete(ACCOUNTS_SERVICE_PATH + "/" + jacekAccountId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(ACCOUNT_IS_USED_IN_FILTER))));
  }

  @Test
  public void shouldReturnErrorWhenTryingToDeleteCategoryUsedInFilter() throws Exception {
    // given
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);

    Filter filter = filterCarExpenses();
    filter.setCategoryIds(convertIdsToList(carCategoryId));
    callRestServiceToAddFilterAndReturnId(filter, token);

    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + carCategoryId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", Matchers.is(getMessage(CATEGORY_IS_USED_IN_FILTER))));
  }

}

