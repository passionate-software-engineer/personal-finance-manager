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

  @Test
  public void shouldGetCategoryById() {

    //given
    when(categoryRepository.findById(categoryCar().getId())).thenReturn(Optional.of(categoryCar()));

    //when
    Category result = categoryService.getCategoryById(categoryCar().getId()).orElse(null);

    //then
    verify(categoryRepository).findById(categoryCar().getId());
    assertNotNull(result);
    assertThat(result.getId(), is(equalTo(categoryCar().getId())));
    assertThat(result.getName(), is(equalTo(categoryCar().getName())));
    assertThat(result.getParentCategory(), is(equalTo(null)));
  }

  @Test
  public void shouldGetCategories() {

    //given
    when(categoryRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(categoryCar(), categoryHome())));

    //when
    List<Category> result = categoryService.getCategories();

    //then
    verify(categoryRepository).findAll();
    assertNotNull(result);
    assertThat(result.size(), is(2));
    assertThat(result, containsInAnyOrder(categoryCar(), categoryHome()));
  }

  @Test
  public void shouldAddCategory() {

    //given
    when(categoryRepository.save(categoryCar())).thenReturn(categoryCar());

    //when
    categoryService.addCategory(categoryCar());

    //then
    verify(categoryRepository).save(categoryCar());
  }

  @Test
  public void shouldAddCategoryWithParentCategory() {
    //given
    when(categoryRepository.save(categoryCar())).thenReturn(categoryCar());
    when(categoryRepository.findById(categoryCar().getId())).thenReturn(Optional.of(categoryCar()));
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar());
    when(categoryRepository.save(categoryOil)).thenReturn(categoryOil);

    //when
    categoryService.addCategory(categoryCar());
    categoryService.addCategory(categoryOil);

    //then
    verify(categoryRepository).save(categoryCar());
    verify(categoryRepository).findById(categoryCar().getId());
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
    when(categoryRepository.findById(categoryCar().getId())).thenReturn(Optional.of(categoryCar()));
    when(categoryRepository.save(categoryCar())).thenReturn(categoryCar());

    //when
    categoryService.updateCategory(categoryCar().getId(), categoryCar());

    //then
    verify(categoryRepository).findById(categoryCar().getId());
    verify(categoryRepository).save(categoryCar());

  }

  @Test
  public void shouldUpdateCategoryWithParentCategory() {

    //given
    Category categoryCar = categoryCar();
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar);
    when(categoryRepository.findById(categoryOil.getId())).thenReturn(Optional.of(categoryOil));
    when(categoryRepository.findById(categoryCar.getId())).thenReturn(Optional.of(categoryCar));
    when(categoryRepository.save(categoryOil)).thenReturn(categoryOil);

    //when
    categoryService.updateCategory(categoryOil.getId(), categoryOil);

    //then
    verify(categoryRepository).findById(categoryOil.getId());
    verify(categoryRepository).findById(categoryCar.getId());
    verify(categoryRepository).save(categoryOil);
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExist() {

    //given
    long id = 1;
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.updateCategory(id, new Category()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + id + " does not exist in database")));
  }

  @Test
  public void shouldThrowExceptionCausedByIdOfParentCategoryNotExist() {

    //given
    Category categoryOil = categoryOil();
    categoryOil.setParentCategory(categoryCar());
    when(categoryRepository.findById(categoryOil.getId())).thenReturn(Optional.of(categoryOil));
    when(categoryRepository.findById(categoryCar().getId())).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> categoryService.updateCategory(categoryOil.getId(), categoryOil));

    //then
    assertThat(exception.getMessage(), is(equalTo("Category with id : " + categoryCar().getId() + " does not exist in database")));
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
