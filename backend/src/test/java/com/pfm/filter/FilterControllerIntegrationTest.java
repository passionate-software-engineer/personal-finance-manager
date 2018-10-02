package com.pfm.filter;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestFilterProvider.convertAccountIdsToList;
import static com.pfm.helpers.TestFilterProvider.convertCategoryIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterCarExpenses;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestFilterProvider.filterHomeExpensesUpTo200;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FilterControllerIntegrationTest extends IntegrationTestsBase {

  private String token;
  private long userId;

  @Before
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = autheticateUserAndReturnUserToken(userMarian());
  }

  @Test
  public void shouldAddFilter() throws Exception {

    //given
    Long categoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    Long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(filterHomeExpensesUpTo200());
    homeExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryId));
    homeExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountId));

    //when
    Long filterId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd, token);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, homeExpensesFilterToAdd);
    expectedFilter.setUserId(userId);

    Filter actualFilter = getFilterById(filterId, token);
    assertThat(expectedFilter, is(equalTo(actualFilter)));
  }

  @Test
  public void shouldGetFilterById() throws Exception {

    //given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountId));
    carExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryId));
    long filterId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd, token);

    //when
    Filter outputFilter = getFilterById(filterId, token);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, carExpensesFilterToAdd);
    expectedFilter.setUserId(userId);
    assertThat(expectedFilter, is(equalTo(outputFilter)));
  }

  @Test
  public void shouldGetAllFilters() throws Exception {

    //given
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long categoryHomeId = callRestToAddCategoryAndReturnId(categoryHome(), token);
    long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(filterHomeExpensesUpTo200());
    homeExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryHomeId));
    final long filterHomeExpensesId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd, token);

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountMbankId, accountJacekId));
    carExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryCarId));
    final long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd, token);

    FilterRequest foodExpensesFilterToAdd = convertFilterToFilterRequest(filterFoodExpenses());
    foodExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountJacekId));
    foodExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryFoodId));
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(foodExpensesFilterToAdd, token);

    //when
    final List<Filter> actualListOfFilters = callRestToGetAllFilters(token);

    //then
    final Filter expectedCarExpensesFilter = convertFilterRequestToFilterAndSetId(filterCarExpensesId, carExpensesFilterToAdd);
    expectedCarExpensesFilter.setUserId(userId);

    final Filter expectedFoodExpensesFilter = convertFilterRequestToFilterAndSetId(filterFoodExpensesId, foodExpensesFilterToAdd);
    expectedFoodExpensesFilter.setUserId(userId);

    final Filter expectedHomeExpensesFilter = convertFilterRequestToFilterAndSetId(filterHomeExpensesId, homeExpensesFilterToAdd);
    expectedHomeExpensesFilter.setUserId(userId);

    assertThat(actualListOfFilters.size(), is(3));
    assertThat(actualListOfFilters, containsInAnyOrder(
        expectedCarExpensesFilter,
        expectedFoodExpensesFilter,
        expectedHomeExpensesFilter
    ));
  }

  @Test
  public void shouldDeleteFilter() throws Exception {

    //given
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filterCarExpenses(), token);
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(filterFoodExpenses(), token);

    //when
    callRestToDeleteFilterById(filterCarExpensesId, token);

    //then
    List<Filter> actualFilters = callRestToGetAllFilters(token);

    assertThat(actualFilters.size(), is(1));

    Filter expectedFoodExpenses = filterFoodExpenses();
    expectedFoodExpenses.setId(filterFoodExpensesId);
    expectedFoodExpenses.setUserId(userId);

    Filter expetedCarExpenses = filterCarExpenses();
    expetedCarExpenses.setId(filterCarExpensesId);
    expetedCarExpenses.setUserId(userId);

    assertThat(actualFilters, contains(expectedFoodExpenses));
    assertThat(actualFilters.contains(expetedCarExpenses), is(false));
  }

  @Test
  public void shouldUpdateFilter() throws Exception {

    //given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000(), token);
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filterCarExpenses(), token);

    FilterRequest filterCarExpensesToUpdate = FilterRequest.builder()
        .name("Car expenses between 1000$ and 2000$")
        .priceTo(convertDoubleToBigDecimal(2000))
        .priceFrom(convertDoubleToBigDecimal(1000))
        .description("Car")
        .dateFrom(LocalDate.of(2017, 1, 1))
        .dateTo(LocalDate.of(2017, 1, 31))
        .categoryIds(convertCategoryIdsToList(categoryId))
        .accountIds(convertAccountIdsToList(accountId))
        .build();

    //when
    callRestServiceToUpdateFilter(filterCarExpensesId, filterCarExpensesToUpdate, token);

    //then
    Filter updatedFilter = getFilterById(filterCarExpensesId, token);
    final Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterCarExpensesId, filterCarExpensesToUpdate);
    expectedFilter.setUserId(userId);

    assertThat(updatedFilter, is(equalTo(expectedFilter)));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInGetMethod() throws Exception {

    //when
    mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInDeleteMethod() throws Exception {

    //when
    mockMvc
        .perform(delete(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInUpdateMethod() throws Exception {

    //when
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header("Authorization", token)
            .content(json(filterCarExpenses()))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInAddMethod() throws Exception {

    //given
    FilterRequest filterRequestWithValidationErrors = FilterRequest.builder()
        .accountIds(convertAccountIdsToList(1L))
        .categoryIds(convertCategoryIdsToList(1L))
        .dateFrom(LocalDate.of(2018, 1, 1))
        .dateTo(LocalDate.of(2017, 1, 1))
        .priceFrom(convertDoubleToBigDecimal(100))
        .priceTo(convertDoubleToBigDecimal(50))
        .build();

    //when
    mockMvc
        .perform(post(FILTERS_SERVICE_PATH)
            .header("Authorization", token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInAddMethodSecondCase() throws Exception {

    //given
    FilterRequest filterRequestWithValidationErrors = FilterRequest.builder()
        .dateFrom(LocalDate.of(2018, 1, 1))
        .priceFrom(convertDoubleToBigDecimal(100))
        .build();

    //when
    mockMvc
        .perform(post(FILTERS_SERVICE_PATH)
            .header("Authorization", token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInUpdateMethod() throws Exception {

    //given
    final long filterId = callRestServiceToAddFilterAndReturnId(filterCarExpenses(), token);
    FilterRequest filterRequestWithValidationErrors = new FilterRequest();
    filterRequestWithValidationErrors.setName(" ");
    filterRequestWithValidationErrors.setAccountIds(convertAccountIdsToList(1L));
    filterRequestWithValidationErrors.setCategoryIds(convertCategoryIdsToList(1L));
    filterRequestWithValidationErrors.setDateFrom(LocalDate.of(2018, 1, 1));
    filterRequestWithValidationErrors.setDateTo(LocalDate.of(2017, 1, 1));
    filterRequestWithValidationErrors.setPriceFrom(convertDoubleToBigDecimal(100));
    filterRequestWithValidationErrors.setPriceTo(convertDoubleToBigDecimal(50));
    filterRequestWithValidationErrors.setDescription("description");

    //when
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + filterId)
            .header("Authorization", token)
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

}

