package com.pfm.auth;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class UserTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = User.class;

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
    final Class<?> classUnderTest = User.UserBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    User user = User.builder().id(5L).build();
    User other = User.builder().id(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsIdWhenBothAreEqual() {
    User user = User.builder().id(1L).build();
    User other = User.builder().id(1L).build();

    assertTrue(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInUsername() {
    User user = User.builder().username("user").build();
    User other = User.builder().username(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInWhenBothAreEqual() {
    User user = User.builder().username("user").build();
    User other = User.builder().username("user").build();

    assertTrue(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPassword() {
    User user = User.builder().password("password").build();
    User other = User.builder().password(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInPasswordWhenBothAreEqual() {
    User user = User.builder().password("password").build();
    User other = User.builder().password("password").build();

    assertTrue(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstName() {
    User user = User.builder().firstName("Sebastian").build();
    User other = User.builder().firstName(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInFirstNameWhenBothAreEqual() {
    User user = User.builder().firstName("Sebastian").build();
    User other = User.builder().firstName("Sebastian").build();

    assertTrue(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastName() {
    User user = User.builder().lastName("Malik").build();
    User other = User.builder().lastName(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInLastNameWhenBothAreEqual() {
    User user = User.builder().lastName("Malik").build();
    User other = User.builder().lastName("Malik").build();

    assertTrue(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInToken() {
    User user = User.builder().token("token").build();
    User other = User.builder().token(null).build();

    assertFalse(user.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInTokenWhenBothAreEqual() {
    User user = User.builder().token("token").build();
    User other = User.builder().token("token").build();

    assertTrue(user.equals(other));
  }

}
