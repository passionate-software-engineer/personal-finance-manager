package com.pfm.filter;

import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.pfm.auth.UserProvider;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.history.HistoryEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

class FilterControllerTransactionalTest extends IntegrationTestsBase {

  @SpyBean
  private HistoryEntryService historyEntryService;

  @MockBean
  private UserProvider userProvider;

  @SpyBean
  private FilterService filterService;

  @Autowired
  private FilterController filterController;

  @Override
  @BeforeEach
  public void before() {
    super.before();
    userId = userService.registerUser(userZdzislaw()).getId();
    when(userProvider.getCurrentUserId()).thenReturn(userId);
  }

  @Test
  void shouldRollbackTransactionWhenFilterAddFailed() {
    // given
    Filter filter = filterFoodExpenses();
    doThrow(IllegalStateException.class).when(historyEntryService).addHistoryEntryOnAdd(any(Object.class), any(Long.class));

    // when
    try {
      filterController.addFilter(convertFilterToFilterRequest(filter));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(filterService.getAllFilters(userId), hasSize(0));
  }

  @Test
  void shouldRollbackTransactionWhenFilterUpdateFailed() {
    // given
    Filter filter = filterFoodExpenses();
    final Long filterId = filterService.addFilter(userId, filter).getId();

    Filter updatedFilter = filterFoodExpenses();
    updatedFilter.setName("updatedName");

    doThrow(IllegalStateException.class).when(filterService).updateFilter(any(Long.class), any(Long.class), any(Filter.class));

    // when
    try {
      filterController.updateFilter(filterId, convertFilterToFilterRequest(filter));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));

  }

  @Test
  void shouldRollbackTransactionWhenFilterDeleteFailed() {
    // given
    Filter filter = filterFoodExpenses();
    final Long filterId = filterService.addFilter(userId, filter).getId();

    doThrow(IllegalStateException.class).when(filterService).deleteFilter(filterId);

    // when
    try {
      filterController.deleteFilter(filterId);
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));
  }
}
