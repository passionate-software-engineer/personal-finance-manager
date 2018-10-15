package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryService categoryService;

  private static long FAKE_USER_ID = -1;

  @Test
  public void shouldGetCategoryById() {

    //given
    Category categoryCar = categoryCar();
    when(categoryRepository.findById(categoryCar.getId())).thenReturn(Optional.of(categoryCar));

    //when
    Category result = categoryService.getCategoryById(categoryCar.getId()).orElse(null);

    //then
    verify(categoryRepository).findById(categoryCar.getId());
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
    when(categoryRepository.findByUserId(FAKE_USER_ID)).thenReturn(new ArrayList<>(Arrays.asList(categoryCar, categoryHome)));

    //when
    List<Category> result = categoryService.getCategories(FAKE_USER_ID);

    //then
    verify(categoryRepository).findByUserId(FAKE_USER_ID);
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
    categoryService.addCategory(category, FAKE_USER_ID);

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

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      categoryService.addCategory(category);
    });

    //then
    assertThat(exception.getMessage(), is(equalTo("Cannot find parent category with id -1")));
  }

  @Test
  public void shouldAddCategoryWithParentCategory() {
    //given
    Category categoryCar = categoryCar();
    when(categoryRepository.save(categoryCar)).thenReturn(categoryCar);
    when(categoryRepository.findByIdAndUserId(categoryCar.getId(), FAKE_USER_ID)).thenReturn(Optional.of(categoryCar));
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar);
    when(categoryRepository.save(categoryOil)).thenReturn(categoryOil);

    //when
    categoryService.addCategory(categoryCar, FAKE_USER_ID);
    categoryService.addCategory(categoryOil, FAKE_USER_ID);

    //then
    verify(categoryRepository).save(categoryCar);
    verify(categoryRepository).findByIdAndUserId(categoryCar.getId(), FAKE_USER_ID);
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
    when(categoryRepository.findByIdAndUserId(category.getId(), FAKE_USER_ID)).thenReturn(Optional.of(category));
    when(categoryRepository.save(category)).thenReturn(category);

    //when
    categoryService.updateCategory(category.getId(), category, FAKE_USER_ID);

    //then
    verify(categoryRepository).findByIdAndUserId(category.getId(), FAKE_USER_ID);
    verify(categoryRepository).save(category);

  }

  @Test
  public void shouldUpdateCategoryWithParentCategory() {

    //given
    Category categoryCar = categoryCar();
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar);
    when(categoryRepository.findByIdAndUserId(categoryOil.getId(), FAKE_USER_ID)).thenReturn(Optional.of(categoryOil));
    when(categoryRepository.findByIdAndUserId(categoryCar.getId(), FAKE_USER_ID)).thenReturn(Optional.of(categoryCar));
    when(categoryRepository.save(categoryOil)).thenReturn(categoryOil);

    //when
    categoryService.updateCategory(categoryOil.getId(), categoryOil, FAKE_USER_ID);

    //then
    verify(categoryRepository).findByIdAndUserId(categoryOil.getId(), FAKE_USER_ID);
    verify(categoryRepository).findByIdAndUserId(categoryCar.getId(), FAKE_USER_ID);
    verify(categoryRepository).save(categoryOil);
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExist() {

    //given
    long id = 1;
    when(categoryRepository.findByIdAndUserId(id, FAKE_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.updateCategory(id, new Category(), FAKE_USER_ID));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + id + " does not exist in database")));
  }

  @Test
  public void shouldThrowExceptionCausedByIdOfParentCategoryNotExist() {

    //given
    Category categoryOil = categoryOil();
    Category categoryCar = categoryCar();
    categoryOil.setParentCategory(categoryCar);
    when(categoryRepository.findByIdAndUserId(categoryOil.getId(), FAKE_USER_ID)).thenReturn(Optional.of(categoryOil));
    when(categoryRepository.findByIdAndUserId(categoryCar.getId(), FAKE_USER_ID)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class,
        () -> categoryService.updateCategory(categoryOil.getId(), categoryOil, FAKE_USER_ID));

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
    when(categoryRepository.findById(notExistingCategoryId)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      categoryService.canBeParentCategory(1, notExistingCategoryId);
    });

    //then
    assertThat(exception.getMessage(), is(equalTo("Received parent category id (" + notExistingCategoryId + ") which does not exists in database")));
  }

}
