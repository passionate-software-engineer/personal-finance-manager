package com.pfm.category;

import static com.pfm.helpers.TestCategoryProvider.categoryAnimals;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static com.pfm.history.DifferenceProvider.UPDATE_ENTRY_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryDifferenceProviderTest {

  @Test
  void getDifferencesCategoryTest() {
    Category category = categoryOil();

    Category categoryWithChanges = categoryFood();

    //when
    List<String> differences = category.getDifferences(categoryWithChanges);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", category.getName(), categoryWithChanges.getName()));

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesParentCategoryNameChangedCaseTest() {
    Category category = categoryOil();
    category.setParentCategory(categoryCar());

    Category categoryWithChanges = categoryAnimals();
    categoryWithChanges.setParentCategory(categoryHome());

    //when
    List<String> differences = category.getDifferences(categoryWithChanges);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", category.getName(), categoryWithChanges.getName()));
    expected.add(String
        .format(UPDATE_ENTRY_TEMPLATE, "parent category", category.getParentCategory().getName(), categoryWithChanges.getParentCategory().getName()));

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesNoChangesCaseTest() {
    Category category = categoryOil();
    category.setParentCategory(categoryOil());

    Category categoryWithChanges = categoryOil();
    categoryWithChanges.setParentCategory(categoryOil());

    //when
    List<String> differences = category.getDifferences(categoryWithChanges);

    //then
    assertThat(differences, equalTo(new ArrayList<>()));

  }

  @Test
  void getDifferencesParentCategoryChangedFromMainCategoryTest() {
    Category category = categoryOil();

    Category categoryWithChanges = categoryOil();
    categoryWithChanges.setParentCategory(categoryCar());

    //when
    List<String> differences = category.getDifferences(categoryWithChanges);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String
        .format(UPDATE_ENTRY_TEMPLATE, "parent category", "'Main Category'", categoryWithChanges.getParentCategory().getName()));

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getDifferencesParentCategoryChangedToMainCategoryTest() {
    Category category = categoryOil();
    category.setParentCategory(categoryCar());

    Category categoryWithChanges = categoryOil();

    //when
    List<String> differences = category.getDifferences(categoryWithChanges);

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String
        .format(UPDATE_ENTRY_TEMPLATE, "parent category", category.getParentCategory().getName(), "'Main Category'"));

    assertThat(differences, equalTo(expected));

  }

  @Test
  void getObjectPropertiesWithValuesParentCategoryExistTest() {

    //given
    Category category = categoryOil();
    category.setParentCategory(categoryCar());

    //when
    List<String> objectPropertiesWithValues = category.getObjectPropertiesWithValues();

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "name", category.getName()));
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "parent category", category.getParentCategory().getName()));

    assertThat(objectPropertiesWithValues, equalTo(expected));

  }

  @Test
  void getObjectPropertiesWithValuesParentCategoryDoesNotExistTest() {

    //given
    Category category = categoryCar();

    //when
    List<String> objectPropertiesWithValues = category.getObjectPropertiesWithValues();

    //then
    List<String> expected = new ArrayList<>();
    expected.add(String.format(ENTRY_VALUES_TEMPLATE, "name", category.getName()));

    assertThat(objectPropertiesWithValues, equalTo(expected));

  }

  @Test
  void getObjectDescriptiveNameTest() {

    //given
    Category category = categoryCar();

    //when
    assertThat(category.getName(), equalTo(category.getObjectDescriptiveName()));
  }
}