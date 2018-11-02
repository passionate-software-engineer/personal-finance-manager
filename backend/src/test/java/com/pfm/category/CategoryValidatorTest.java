package com.pfm.category;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryValidatorTest {

  private static final long MOCK_USER_ID = 1;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryValidator categoryValidator;

  @Test
  public void validateCategoryForUpdate() {

    //given
    long id = 1L;
    when(categoryService.getCategoryByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> categoryValidator.validateCategoryForUpdate(id, MOCK_USER_ID, new Category()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id: " + id + " does not exist in database")));
  }
}