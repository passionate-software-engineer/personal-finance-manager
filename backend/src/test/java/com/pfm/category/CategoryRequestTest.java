package com.pfm.category;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class CategoryRequestTest {

  @Test
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = CategoryRequest.CategoryRequestBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
}