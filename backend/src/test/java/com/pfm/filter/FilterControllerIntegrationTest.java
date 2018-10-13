package com.pfm.filter;

import static com.pfm.test.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.test.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.test.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.test.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.test.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.test.helpers.TestFilterProvider.convertAccountIdsToList;
import static com.pfm.test.helpers.TestFilterProvider.convertCategoryIdsToList;
import static com.pfm.test.helpers.TestFilterProvider.filterCarExpenses;
import static com.pfm.test.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.test.helpers.TestFilterProvider.filterHomeExpensesUpTo200;
import static com.pfm.test.helpers.TestHelper.convertDoubleToBigDecimal;
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

import com.pfm.test.helpers.IntegrationTestsBase;
import java.time.LocalDate;
import java.util.List;
import org.junit.Test;

public class FilterControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddFilter() throws Exception {

    //given
    Long categoryId = callRestToAddCategoryAndReturnId(categoryFood());
    Long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(filterHomeExpensesUpTo200());
    homeExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryId));
    homeExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountId));

    //when
    Long filterId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, homeExpensesFilterToAdd);

    Filter actualFilter = getFilterById(filterId);
    assertThat(expectedFilter, is(equalTo(actualFilter)));
  }

  @Test
  public void shouldGetFilterById() throws Exception {

    //given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar());
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountId));
    carExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryId));
    long filterId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd);

    //when
    Filter outputFilter = getFilterById(filterId);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, carExpensesFilterToAdd);
    assertThat(expectedFilter, is(equalTo(outputFilter)));
  }

  @Test
  public void shouldGetAllFilters() throws Exception {

    //given
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood());
    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar());
    long categoryHomeId = callRestToAddCategoryAndReturnId(categoryHome());
    long accountJacekId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10());

    FilterRequest homeExpensesFilterToAdd = convertFilterToFilterRequest(filterHomeExpensesUpTo200());
    homeExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryHomeId));
    final long filterHomeExpensesId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd);

    FilterRequest carExpensesFilterToAdd = convertFilterToFilterRequest(filterCarExpenses());
    carExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountMbankId, accountJacekId));
    carExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryCarId));
    final long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd);

    FilterRequest foodExpensesFilterToAdd = convertFilterToFilterRequest(filterFoodExpenses());
    foodExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountJacekId));
    foodExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryFoodId));
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(foodExpensesFilterToAdd);

    //when
    List<Filter> actualListOfFilters = callRestToGetAllFilters();

    //then
    assertThat(actualListOfFilters.size(), is(3));
    assertThat(actualListOfFilters, containsInAnyOrder(
        convertFilterRequestToFilterAndSetId(filterCarExpensesId, carExpensesFilterToAdd),
        convertFilterRequestToFilterAndSetId(filterFoodExpensesId, foodExpensesFilterToAdd),
        convertFilterRequestToFilterAndSetId(filterHomeExpensesId, homeExpensesFilterToAdd)
    ));
  }

  @Test
  public void shouldDeleteFilter() throws Exception {

    //given
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filterCarExpenses());
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(filterFoodExpenses());

    //when
    callRestToDeleteFilterById(filterCarExpensesId);

    //then
    List<Filter> actualFilters = callRestToGetAllFilters();

    assertThat(actualFilters.size(), is(1));

    Filter expectedFoodExpenses = filterFoodExpenses();
    expectedFoodExpenses.setId(filterFoodExpensesId);
    Filter expetedCarExpenses = filterCarExpenses();
    expetedCarExpenses.setId(filterCarExpensesId);

    assertThat(actualFilters, contains(expectedFoodExpenses));
    assertThat(actualFilters.contains(expetedCarExpenses), is(false));
  }

  @Test
  public void shouldUpdateFilter() throws Exception {

    //given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar());
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(filterCarExpenses());

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
    callRestServiceToUpdateFilter(filterCarExpensesId, filterCarExpensesToUpdate);

    //then
    Filter updatedFilter = getFilterById(filterCarExpensesId);
    assertThat(updatedFilter, is(equalTo(convertFilterRequestToFilterAndSetId(filterCarExpensesId, filterCarExpensesToUpdate))));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInGetMethod() throws Exception {

    //when
    mockMvc
        .perform(get(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInDeleteMethod() throws Exception {

    //when
    mockMvc
        .perform(delete(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingIdInUpdateMethod() throws Exception {

    //when
    mockMvc
        .perform(put(FILTERS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
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
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByValidationErrorsIdInUpdateMethod() throws Exception {

    //given
    final long filterId = callRestServiceToAddFilterAndReturnId(filterCarExpenses());
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
            .content(json(filterRequestWithValidationErrors))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

}

