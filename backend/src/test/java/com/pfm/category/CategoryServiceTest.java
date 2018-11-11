package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  private static final long MOCK_USER_ID = -1;

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryService categoryService;

  @Test
  public void shouldGetCategoryById() {

    //given
    Category categoryCar = categoryCar();
    when(categoryRepository.findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID)).thenReturn(Optional.of(categoryCar));

    //when
    Category result = categoryService.getCategoryByIdAndUserId(categoryCar.getId(), MOCK_USER_ID).orElse(null);

    //then
    verify(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);
    assertNotNull(result);
    assertThat(result.getId(), is(equalTo(categoryCar.getId())));
    assertThat(result.getName(), is(equalTo(categoryCar.getName())));
    assertThat(result.getParentCategory(), is(equalTo(null)));
  }

  @Test
  public void shouldGetCategories() {

    //given
    Category categoryCar = categoryCar();
    Category categoryHome = categoryHome();
    when(categoryRepository.findByUserId(MOCK_USER_ID)).thenReturn(new ArrayList<>(Arrays.asList(categoryCar, categoryHome)));

    //when
    List<Category> result = categoryService.getCategories(MOCK_USER_ID);

    //then
    verify(categoryRepository).findByUserId(MOCK_USER_ID);
    assertNotNull(result);
    assertThat(result.size(), is(2));
    assertThat(result, containsInAnyOrder(categoryCar, categoryHome));
  }

  @Test
  public void shouldAddCategory() {

    //given

    Category category = categoryCar();
    when(categoryRepository.save(category)).thenReturn(category);

    //when
    categoryService.addCategory(category, MOCK_USER_ID);

    //then
    verify(categoryRepository).save(category);
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenNotExistingParentIdWasProvided() {

    //given
    Category category = Category.builder()
        .name("does not matter")
        .parentCategory(
            Category.builder()
                .id(-1L)
                .build()
        )
        .build();

    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.addCategory(category, MOCK_USER_ID));

    //then
    assertThat(exception.getMessage(), is(equalTo("Cannot find parent category with id -1")));
  }

  @Test
  public void shouldAddCategoryWithParentCategory() {
    //given
    Category categoryCar = categoryCar();
    doReturn(categoryCar).when(categoryRepository).save(categoryCar);
    doReturn(Optional.of(categoryCar)).when(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);

    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar);
    doReturn(categoryOil).when(categoryRepository).save(categoryOil);

    //when
    categoryService.addCategory(categoryCar, MOCK_USER_ID);
    categoryService.addCategory(categoryOil, MOCK_USER_ID);

    //then
    verify(categoryRepository).save(categoryCar);
    verify(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);
    verify(categoryRepository).save(categoryOil);
  }

  @Test
  public void shouldDeleteCategory() {

    //given
    long id = 1;
    doNothing().when(categoryRepository).deleteById(id);

    //when
    categoryService.deleteCategory(id);

    //then
    verify(categoryRepository).deleteById(id);
  }

  @Test
  public void shouldUpdateCategory() {

    //given
    Category category = categoryCar();
    when(categoryRepository.findByIdAndUserId(category.getId(), MOCK_USER_ID)).thenReturn(Optional.of(category));
    when(categoryRepository.save(category)).thenReturn(category);

    //when
    categoryService.updateCategory(category.getId(), MOCK_USER_ID, category);

    //then
    verify(categoryRepository).findByIdAndUserId(category.getId(), MOCK_USER_ID);
    verify(categoryRepository).save(category);

  }

  @Test
  public void shouldUpdateCategoryWithParentCategory() {

    //given
    Category categoryCar = categoryCar();
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar);
    doReturn(Optional.of(categoryOil)).when(categoryRepository).findByIdAndUserId(categoryOil.getId(), MOCK_USER_ID);
    doReturn(Optional.of(categoryCar)).when(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);
    doReturn(categoryOil).when(categoryRepository).save(categoryOil);

    //when
    categoryService.updateCategory(categoryOil.getId(), MOCK_USER_ID, categoryOil);

    //then
    verify(categoryRepository).findByIdAndUserId(categoryOil.getId(), MOCK_USER_ID);
    verify(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);
    verify(categoryRepository).save(categoryOil);
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExistInUpdateMethod() {

    //given
    long id = 1;
    when(categoryRepository.findByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.getCategoryFromDbByIdAndUserId(id, MOCK_USER_ID));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + id + " does not exist in database")));
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExistInGetMethod() {

    //given
    long id = 1;
    when(categoryRepository.findByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.updateCategory(id, MOCK_USER_ID, new Category()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + id + " does not exist in database")));
  }

  @Test
  public void shouldThrowExceptionCausedByIdOfParentCategoryNotExist() {

    //given
    Category categoryOil = categoryOil();
    Category categoryCar = categoryCar();
    categoryOil.setParentCategory(categoryCar);

    doReturn(Optional.of(categoryOil)).when(categoryRepository).findByIdAndUserId(categoryOil.getId(), MOCK_USER_ID);
    doReturn(Optional.empty()).when(categoryRepository).findByIdAndUserId(categoryCar.getId(), MOCK_USER_ID);

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> categoryService.updateCategory(categoryOil.getId(), MOCK_USER_ID, categoryOil));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + categoryCar.getId() + " does not exist in database")));
  }

  @Test
  public void shouldCheckIfCategoryExist() {

    //given
    long id = 1;
    when(categoryRepository.existsById(id)).thenReturn(true);

    //when
    categoryService.idExist(id);

    //then
    verify(categoryRepository).existsById(id);
  }

  @Test
  public void shouldThrowExceptionWhenPassedParentCategoryWhichDoesNotExist() {

    //given
    long notExistingCategoryId = 5L;
    when(categoryRepository.findByIdAndUserId(notExistingCategoryId, MOCK_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> categoryService.canBeParentCategory(1, notExistingCategoryId, MOCK_USER_ID));

    //then
    assertThat(exception.getMessage(), is(equalTo("Received parent category id (" + notExistingCategoryId + ") which does not exists in database")));
  }

}