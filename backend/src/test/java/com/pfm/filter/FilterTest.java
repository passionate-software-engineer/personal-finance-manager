package com.pfm.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
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
    final Class<?> classUnderTest = Filter.FilterBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyMissingCaseInName() {
    Filter filter = Filter.builder().name("Name").build();
    Filter other = Filter.builder().id(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseAccountIds() {
    Filter filter = Filter.builder().accountIds(Arrays.asList(1L)).build();
    Filter other = Filter.builder().accountIds(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseCategroyIds() {
    Filter filter = Filter.builder().categoryIds(Arrays.asList(1L)).build();
    Filter other = Filter.builder().categoryIds(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCasePriceTo() {
    Filter filter = Filter.builder().priceTo(BigDecimal.TEN).build();
    Filter other = Filter.builder().priceTo(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCasePriceFrom() {
    Filter filter = Filter.builder().priceFrom(BigDecimal.TEN).build();
    Filter other = Filter.builder().priceFrom(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseDateTo() {
    Filter filter = Filter.builder().dateTo(LocalDate.now()).build();
    Filter other = Filter.builder().dateTo(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseDateFrom() {
    Filter filter = Filter.builder().dateFrom(LocalDate.now()).build();
    Filter other = Filter.builder().dateFrom(null).build();

    assertFalse(filter.equals(other));
  }

  @Test
  public void shouldVerifyMissingCaseDescription() {
    Filter filter = Filter.builder().description("description").build();
    Filter other = Filter.builder().description(null).build();

    assertFalse(filter.equals(other));
  }
}
