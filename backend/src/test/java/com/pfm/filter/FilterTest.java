package com.pfm.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class FilterTest {

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToString() {
    // given
    final Class<?> classUnderTest = Filter.class;

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
