package com.pfm.account;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class AccountTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = Account.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = Account.AccountBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

}
