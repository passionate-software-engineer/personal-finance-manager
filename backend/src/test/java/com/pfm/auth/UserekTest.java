package com.pfm.auth;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class UserekTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = Userek.class;

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
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = Userek.UserekBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
  
  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    Userek userek = Userek.builder().id(5L).build();
    Userek other = Userek.builder().id(null).build();

    assertFalse(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsIdWhenBothAreEqual() {
    Userek userek = Userek.builder().id(1L).build();
    Userek other = Userek.builder().id(1L).build();

    assertTrue(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInUsername() {
    Userek userek = Userek.builder().username("userek").build();
    Userek other = Userek.builder().username(null).build();

    assertFalse(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInWhenBothAreEqual() {
    Userek userek = Userek.builder().username("userek").build();
    Userek other = Userek.builder().username("userek").build();

    assertTrue(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPassword() {
    Userek userek = Userek.builder().password("password").build();
    Userek other = Userek.builder().password(null).build();

    assertFalse(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPasswordWhenBothAreEqual() {
    Userek userek = Userek.builder().password("password").build();
    Userek other = Userek.builder().password("password").build();

    assertTrue(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstName() {
    Userek userek = Userek.builder().firstName("Sebastian").build();
    Userek other = Userek.builder().firstName(null).build();

    assertFalse(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstNameWhenBothAreEqual() {
    Userek userek = Userek.builder().firstName("Sebastian").build();
    Userek other = Userek.builder().firstName("Sebastian").build();

    assertTrue(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastName() {
    Userek userek = Userek.builder().lastName("Malik").build();
    Userek other = Userek.builder().lastName(null).build();

    assertFalse(userek.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastNameWhenBothAreEqual() {
    Userek userek = Userek.builder().lastName("Malik").build();
    Userek other = Userek.builder().lastName("Malik").build();

    assertTrue(userek.equals(other));
  }

}
