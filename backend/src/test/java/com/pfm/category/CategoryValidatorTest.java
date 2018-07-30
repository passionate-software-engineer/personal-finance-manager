package com.pfm.category;

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
    Category categoryToVerify = new Category(1L, "Food", null);
    when(categoryService.getCategoryById(1)).thenReturn(Optional.empty());

    //then
    categoryValidator.validateCategoryForUpdate(1, categoryToVerify);
  }
}