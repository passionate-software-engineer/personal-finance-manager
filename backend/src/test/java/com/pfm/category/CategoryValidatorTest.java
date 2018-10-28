package com.pfm.category;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.config.MessagesProvider;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryValidatorTest {

  private long mockUserId = 1;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryValidator categoryValidator;

  @Test
  public void validateCategoryForUpdate() {

    //given
    long id = 1L;
    when(categoryService.getCategoryByIdAndUserId(id, mockUserId)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> categoryValidator.validateCategoryForUpdate(id, mockUserId, new Category()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id: " + id + " does not exist in database")));
  }

  @Test
  public void validateCategoryForUpdateWhenParentCategoryIdIsNull() {

    //given
    Category category = Category.builder()
        .name("Category")
        .parentCategory(Category.builder().build())
        .build();

    long id = 1L;
    when(categoryService.getCategoryByIdAndUserId(id, mockUserId)).thenReturn(Optional.of(category));

    //when
    List<String> validationResults = categoryValidator.validateCategoryForUpdate(id, mockUserId, category);

    //then
    assertThat(validationResults, hasSize(1));
    assertThat(validationResults, contains(MessagesProvider.getMessage(MessagesProvider.PROVIDED_PARENT_CATEGORY_ID_IS_EMPTY)));
  }
}