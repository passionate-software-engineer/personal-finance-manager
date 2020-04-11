package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
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

class CategoryControllerTransactionalTest extends IntegrationTestsBase {

  @SpyBean
  private HistoryEntryService historyEntryService;

  @MockBean
  private UserProvider userProvider;

  @SpyBean
  private CategoryService categoryService;

  @Autowired
  private CategoryController categoryController;

  @Override
  @BeforeEach
  public void before() {
    super.before();
    userId = userService.registerUser(userZdzislaw()).getId();
    when(userProvider.getCurrentUserId()).thenReturn(userId);
  }

  @Test
  void shouldRollbackTransactionWhenCategoryAddFailed() {
    //given
    Category category = categoryCar();
    doThrow(IllegalStateException.class).when(historyEntryService).addHistoryEntryOnAdd(any(Object.class), any(Long.class));

    // when
    try {
      categoryController.addCategory(convertCategoryToCategoryAddRequest(category));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(categoryService.getCategories(userId), hasSize(0));
  }

  @Test
  void shouldRollbackTransactionWhenCategoryUpdateFailed() {
    //given
    Category category = categoryCar();
    final Long categoryId = categoryService.addCategory(category, userId).getId();

    Category updatedCategory = categoryCar();
    updatedCategory.setName("updatedName");

    doThrow(IllegalStateException.class).when(categoryService).updateCategory(any(Long.class), any(Long.class), any(Category.class));

    // when
    try {
      categoryController.updateCategory(categoryId, convertCategoryToCategoryUpdateRequest(updatedCategory));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));
  }

  @Test
  void shouldRollbackTransactionWhenCategoryDeleteFailed() {
    //given
    Category category = categoryFood();
    final Long categoryId = categoryService.addCategory(category, userId).getId();

    doThrow(IllegalStateException.class).when(categoryService).deleteCategory(categoryId);

    // when
    try {
      categoryController.deleteCategory(categoryId);
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));
  }
}
