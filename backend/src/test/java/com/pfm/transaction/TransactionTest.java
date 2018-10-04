package com.pfm.transaction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class TransactionTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = Transaction.class;

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
  public void shouldVerifyMissingCaseInEqualsId() {
    Transaction transaction = Transaction.builder().id(5L).build();
    Transaction other = Transaction.builder().id(null).build();

    assertFalse(transaction.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsDescription() {
    Transaction transaction = Transaction.builder().description("abc").build();
    Transaction other = Transaction.builder().description(null).build();

    assertFalse(transaction.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsPrice() {
    Transaction transaction = Transaction.builder()
        .accountPriceEntries(new ArrayList<>())
        .build();

    Transaction other = Transaction.builder()
        .accountPriceEntries(null)
        .build();

    assertFalse(transaction.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseInEqualsDate() {
    Transaction transaction = Transaction.builder().date(LocalDate.now()).build();
    Transaction other = Transaction.builder().date(null).build();

    assertFalse(transaction.equals(other));
  }

  @Test
  public void shouldVerifyToStringInTransactionRequestBuilder() {
    // given
    final Class<?> classUnderTest = Transaction.TransactionBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }
}
