package com.pfm.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class UserDetailsTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = UserDetails.class;

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
  public void shouldVerifyMissingCaseInEquals() {
    UserDetails userDetails = UserDetails.builder().id(5L).firstName("firstName").lastName("lastName").username("userName").token("token").build();
    UserDetails other = UserDetails.builder().id(5L).firstName("firstName").lastName("lastName").username("userName").token("token").build();

    assertTrue(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    UserDetails userDetails = UserDetails.builder().id(5L).build();
    UserDetails other = UserDetails.builder().id(null).build();

    assertFalse(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsUsername() {
    UserDetails userDetails = UserDetails.builder().username("username").build();
    UserDetails other = UserDetails.builder().username(null).build();

    assertFalse(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsLastname() {
    UserDetails userDetails = UserDetails.builder().lastName("lastname").build();
    UserDetails other = UserDetails.builder().lastName(null).build();

    assertFalse(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsFirstname() {
    UserDetails userDetails = UserDetails.builder().firstName("firstName").build();
    UserDetails other = UserDetails.builder().firstName(null).build();

    assertFalse(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsToken() {
    UserDetails userDetails = UserDetails.builder().token("token").build();
    UserDetails other = UserDetails.builder().token(null).build();

    assertFalse(userDetails.equals(other));
  }

  @Test
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = UserDetails.UserDetailsBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

}
