package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.CATEGORY_FOOD_NO_PARENT_CATEGORY;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryValidator categoryValidator;

  @Test
  public void validateCategoryForUpdate() {
    //when
    when(categoryService.getCategoryById(CATEGORY_FOOD_NO_PARENT_CATEGORY.getId()))
        .thenReturn(Optional.empty());

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Category with id: " + CATEGORY_FOOD_NO_PARENT_CATEGORY.getId() + " does not exist in database");

    //then
    categoryValidator.validateCategoryForUpdate(CATEGORY_FOOD_NO_PARENT_CATEGORY.getId(),
        CATEGORY_FOOD_NO_PARENT_CATEGORY);
  }
}