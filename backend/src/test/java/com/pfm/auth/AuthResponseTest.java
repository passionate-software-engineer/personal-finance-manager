package com.pfm.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class AuthResponseTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = AuthResponse.class;

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
    AuthResponse authResponse = AuthResponse.builder().id(5L).firstName("firstName").lastName("lastName").username("userName").token("token").build();
    AuthResponse other = AuthResponse.builder().id(5L).firstName("firstName").lastName("lastName").username("userName").token("token").build();

    assertTrue(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    AuthResponse authResponse = AuthResponse.builder().id(5L).build();
    AuthResponse other = AuthResponse.builder().id(null).build();

    assertFalse(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsUsername() {
    AuthResponse authResponse = AuthResponse.builder().username("username").build();
    AuthResponse other = AuthResponse.builder().username(null).build();

    assertFalse(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsLastname() {
    AuthResponse authResponse = AuthResponse.builder().lastName("lastname").build();
    AuthResponse other = AuthResponse.builder().lastName(null).build();

    assertFalse(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsFirstname() {
    AuthResponse authResponse = AuthResponse.builder().firstName("firstName").build();
    AuthResponse other = AuthResponse.builder().firstName(null).build();

    assertFalse(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsToken() {
    AuthResponse authResponse = AuthResponse.builder().token("token").build();
    AuthResponse other = AuthResponse.builder().token(null).build();

    assertFalse(authResponse.equals(other));
  }

  @Test
  public void shouldVerifyToString() {
    // given
    final Class<?> classUnderTest = AuthResponse.AuthResponseBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

}
