package com.pfm.export;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.Test;
import pl.pojo.tester.api.assertion.Method;

public class ExportResultTest {

  @Test
  public void shouldVerifyToStringInExportAccountPriceEntryBuilder() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportAccountPriceEntry.ExportAccountPriceEntryBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyToStringInExportAccountBuilder() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportAccount.ExportAccountBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyToStringInExportTransactionBuilder() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportTransaction.ExportTransactionBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyToStringInExportPeriodBuilder() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportPeriod.ExportPeriodBuilder.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .areWellImplemented();
  }

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToStringInExportAccount() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportAccount.class;

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
  public void shouldVerifyEqualsAndHashCodeAndToStringInExportAccountPriceEntry() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportAccountPriceEntry.class;

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
  public void shouldVerifyEqualsAndHashCodeAndToStringInExportTransaction() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportTransaction.class;

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
  public void shouldVerifyEqualsAndHashCodeAndToStringInExportPeriod() {
    // given
    final Class<?> classUnderTest = ExportResult.ExportPeriod.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.SETTER)
        .areWellImplemented();
  }

  // TODO create parametrized tests taking class name and checking those values

  @Test
  public void shouldVerifyEqualsAndHashCodeAndToStringInExportResult() {
    // given
    final Class<?> classUnderTest = ExportResult.class;

    // when

    // then
    assertPojoMethodsFor(classUnderTest)
        .testing(Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.SETTER)
        .areWellImplemented();
  }

}
