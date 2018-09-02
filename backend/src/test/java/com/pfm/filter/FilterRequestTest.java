package com.pfm.filter;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class FilterRequestTest {

  @Test
  public void shouldVerifyToString() {

    // given
    final Class<?> classUnderTest = FilterRequest.FilterRequestBuilder.class;

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
}

