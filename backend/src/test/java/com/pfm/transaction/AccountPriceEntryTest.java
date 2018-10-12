package com.pfm.transaction;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class AccountPriceEntryTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToStringInAccountPriceEntry() {
    // given
    final Class<?> classUnderTest = AccountPriceEntry.class;

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
  public void shouldVerifyToStringInAccountPriceEntryBuilder() {
    // given
    final Class<?> classUnderTest = AccountPriceEntry.AccountPriceEntryBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(AccountPriceEntry.class)
        .verify();
  }

}
