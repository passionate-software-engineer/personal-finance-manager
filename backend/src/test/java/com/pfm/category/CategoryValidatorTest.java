package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.CATEGORY_FOOD_NO_PARENT_CATEGORY;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryValidatorTest {

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryValidator categoryValidator;

  @Test(expected = IllegalStateException.class)
  public void validateCategoryForUpdate() {
    //when
    when(categoryService.getCategoryById(CATEGORY_FOOD_NO_PARENT_CATEGORY.getId()))
        .thenReturn(Optional.empty());

    //then
    categoryValidator.validateCategoryForUpdate(CATEGORY_FOOD_NO_PARENT_CATEGORY.getId(),
        CATEGORY_FOOD_NO_PARENT_CATEGORY);
  }
}