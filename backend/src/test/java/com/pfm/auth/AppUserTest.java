package com.pfm.auth;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class AppUserTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = AppUser.class;

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
    final Class<?> classUnderTest = AppUser.AppUserBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
  
  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    AppUser appUser = AppUser.builder().id(5L).build();
    AppUser other = AppUser.builder().id(null).build();

    assertFalse(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsIdWhenBothAreEqual() {
    AppUser appUser = AppUser.builder().id(1L).build();
    AppUser other = AppUser.builder().id(1L).build();

    assertTrue(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInUsername() {
    AppUser appUser = AppUser.builder().username("appUser").build();
    AppUser other = AppUser.builder().username(null).build();

    assertFalse(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInWhenBothAreEqual() {
    AppUser appUser = AppUser.builder().username("appUser").build();
    AppUser other = AppUser.builder().username("appUser").build();

    assertTrue(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPassword() {
    AppUser appUser = AppUser.builder().password("password").build();
    AppUser other = AppUser.builder().password(null).build();

    assertFalse(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPasswordWhenBothAreEqual() {
    AppUser appUser = AppUser.builder().password("password").build();
    AppUser other = AppUser.builder().password("password").build();

    assertTrue(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstName() {
    AppUser appUser = AppUser.builder().firstName("Sebastian").build();
    AppUser other = AppUser.builder().firstName(null).build();

    assertFalse(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstNameWhenBothAreEqual() {
    AppUser appUser = AppUser.builder().firstName("Sebastian").build();
    AppUser other = AppUser.builder().firstName("Sebastian").build();

    assertTrue(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastName() {
    AppUser appUser = AppUser.builder().lastName("Malik").build();
    AppUser other = AppUser.builder().lastName(null).build();

    assertFalse(appUser.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastNameWhenBothAreEqual() {
    AppUser appUser = AppUser.builder().lastName("Malik").build();
    AppUser other = AppUser.builder().lastName("Malik").build();

    assertTrue(appUser.equals(other));
  }

}
