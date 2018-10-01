package com.pfm.category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class CategoryTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = Category.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.SETTER)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    Category category = Category.builder().id(5L).build();
    Category other = Category.builder().id(null).build();

    assertFalse(category.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsName() {
    Category category = Category.builder().name("Oszczednosci na Miodek").build();
    Category other = Category.builder().name(null).build();

    assertFalse(category.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsParentCategory() {
    Category category = Category.builder().parentCategory(new Category()).build();
    Category other = Category.builder().parentCategory(null).build();

    assertFalse(category.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsUserId() {
    Category category = Category.builder().userId(1L).build();
    Category other = Category.builder().userId(null).build();

    assertFalse(category.equals(other));
  }

  @Test
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = Category.CategoryBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

}
