package com.pfm.filter;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.getCategoryRequestCarNoParentCategory;
import static com.pfm.helpers.TestCategoryProvider.getCategoryRequestFoodNoParentCategory;
import static com.pfm.helpers.TestCategoryProvider.getCategoryRequestHomeNoParentCategory;
import static com.pfm.helpers.TestFilterProvider.convertAccountIdsToList;
import static com.pfm.helpers.TestFilterProvider.convertCategoryIdsToList;
import static com.pfm.helpers.TestFilterProvider.getFilterRequestCarExpenses;
import static com.pfm.helpers.TestFilterProvider.getFilterRequestFoodExpenses;
import static com.pfm.helpers.TestFilterProvider.getFilterRequestHomeExpensesUpTo200;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
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
import org.junit.Test;

public class FilterControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddFilter() throws Exception {
    //given
    Long categoryId = callRestToAddCategoryAndReturnId(getCategoryRequestFoodNoParentCategory());
    Long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    FilterRequest homeExpensesFilterToAdd = getFilterRequestFoodExpenses();
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
    long categoryId = callRestToAddCategoryAndReturnId(getCategoryRequestCarNoParentCategory());
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());

    FilterRequest carExpensesFilterToAdd = getFilterRequestCarExpenses();
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
    final long categoryFoodId = callRestToAddCategoryAndReturnId(getCategoryRequestFoodNoParentCategory());
    long categoryCarId = callRestToAddCategoryAndReturnId(getCategoryRequestCarNoParentCategory());
    long categoryHomeId = callRestToAddCategoryAndReturnId(getCategoryRequestHomeNoParentCategory());
    long accountMbankId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long accountDamianId = callRestServiceToAddAccountAndReturnId(ACCOUNT_RAFAL_BALANCE_0);

    FilterRequest homeExpensesFilterToAdd = getFilterRequestHomeExpensesUpTo200();
    homeExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryHomeId));
    final long filterHomeExpensesId = callRestServiceToAddFilterAndReturnId(homeExpensesFilterToAdd);

    FilterRequest carExpensesFilterToAdd = getFilterRequestCarExpenses();
    carExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountDamianId, accountMbankId));
    carExpensesFilterToAdd.setCategoryIds(convertCategoryIdsToList(categoryCarId));
    final long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(carExpensesFilterToAdd);

    FilterRequest foodExpensesFilterToAdd = getFilterRequestFoodExpenses();
    foodExpensesFilterToAdd.setAccountIds(convertAccountIdsToList(accountMbankId));
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
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(getFilterRequestCarExpenses());
    long filterFoodExpensesId = callRestServiceToAddFilterAndReturnId(getFilterRequestFoodExpenses());

    //when
    callRestToDeleteFilterById(filterCarExpensesId);

    //then
    List<Filter> actualFilters = callRestToGetAllFilters();

    assertThat(actualFilters.size(), is(1));
    assertThat(actualFilters, contains(convertFilterRequestToFilterAndSetId(filterFoodExpensesId, getFilterRequestFoodExpenses())));
    assertThat(actualFilters.contains(convertFilterRequestToFilterAndSetId(filterCarExpensesId, getFilterRequestCarExpenses())), is(false));
  }

  @Test
  public void shouldUpdateFilter() throws Exception {
    //given
    long categoryId = callRestToAddCategoryAndReturnId(getCategoryRequestCarNoParentCategory());
    long accountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long filterCarExpensesId = callRestServiceToAddFilterAndReturnId(getFilterRequestCarExpenses());

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
            .content(json(getFilterRequestCarExpenses()))
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
    final long filterId = callRestServiceToAddFilterAndReturnId(getFilterRequestCarExpenses());
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

