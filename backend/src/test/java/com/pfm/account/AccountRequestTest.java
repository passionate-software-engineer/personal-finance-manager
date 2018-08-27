package com.pfm.account;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class AccountRequestTest {

  @Test
  public void shouldVerifyToStringInAccountRequestBuilder() {

    // given
    final Class<?> classUnderTest = AccountRequest.AccountRequestBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
}
