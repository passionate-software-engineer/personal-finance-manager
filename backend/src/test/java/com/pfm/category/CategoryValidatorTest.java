package com.pfm.category;

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
    long id = 1L;
    when(categoryService.getCategoryById(id)).thenReturn(Optional.empty());

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Category with id: " + id + " does not exist in database");

    //then
    categoryValidator.validateCategoryForUpdate(id, new Category());
  }
}