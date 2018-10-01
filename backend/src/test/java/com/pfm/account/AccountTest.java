package com.pfm.account;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import java.math.BigDecimal;
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
        .testing(Method.SETTER)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyMissingCaseInEquals() {
    Account account = Account.builder().id(5L).userId(1L).build();
    Account other = Account.builder().id(5L).userId(1L).build();

    assertTrue(account.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsId() {
    Account account = Account.builder().id(5L).build();
    Account other = Account.builder().id(null).build();

    assertFalse(account.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsName() {
    Account account = Account.builder().name("mBank").build();
    Account other = Account.builder().name(null).build();

    assertFalse(account.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsBalance() {
    Account account = Account.builder().balance(BigDecimal.ONE).build();
    Account other = Account.builder().balance(null).build();

    assertFalse(account.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsUserId() {
    Account account = Account.builder().userId(1L).build();
    Account other = Account.builder().userId(null).build();

    assertFalse(account.equals(other));
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
