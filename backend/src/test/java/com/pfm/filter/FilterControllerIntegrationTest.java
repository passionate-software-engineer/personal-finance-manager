package com.pfm.filter;

import static com.pfm.helpers.TestAccountProvider.getAccountDamianBalance10Request;
import static com.pfm.helpers.TestCategoryProvider.getCategoryFoodNoParentCategoryRequest;
import static com.pfm.helpers.TestFilterProvider.getFilterFoodExpenses;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.pfm.IntegrationTestsBase;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class FilterControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddFilter() throws Exception {

    //given
    Long categoryId = addCategoryAndReturnId(getCategoryFoodNoParentCategoryRequest());
    Long accountId = callRestServiceToAddAccountAndReturnId(getAccountDamianBalance10Request());
    List<Long> accountIds = Collections.singletonList(accountId);
    List<Long> categoryIds = Collections.singletonList(categoryId);

    //when
    Long filterId = callRestServiceToAddFilterAndReturnId(getFilterFoodExpenses(), accountIds,
        categoryIds);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, getFilterFoodExpenses());
    expectedFilter.setAccountIds(accountIds);
    expectedFilter.setCategoryIds(categoryIds);
    Filter actualFilter = getFilterById(filterId);
    assertThat(expectedFilter, is(equalTo(actualFilter)));
  }

  @Test
  public void shouldGetFilterById() throws Exception {
    Long categoryId = addCategoryAndReturnId(getCategoryFoodNoParentCategoryRequest());
    Long accountId = callRestServiceToAddAccountAndReturnId(getAccountDamianBalance10Request());
    List<Long> accountIds = Collections.singletonList(accountId);
    List<Long> categoryIds = Collections.singletonList(categoryId);
    Long filterId = callRestServiceToAddFilterAndReturnId(getFilterFoodExpenses(), accountIds,
        categoryIds);

    //when
    Filter outputFilter = getFilterById(filterId);

    //then
    Filter expectedFilter = convertFilterRequestToFilterAndSetId(filterId, getFilterFoodExpenses());
    assertThat(expectedFilter, is(equalTo(outputFilter)));
  }

}

