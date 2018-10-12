package com.pfm.export;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import com.pfm.category.Category;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
  public void equalsContractExportAccount() {
    EqualsVerifier.forClass(ExportResult.ExportAccount.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
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
  public void equalsContractExportAccountPriceEntry() {
    EqualsVerifier.forClass(ExportResult.ExportAccountPriceEntry.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
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
  public void equalsContractExportTransaction() {
    EqualsVerifier.forClass(ExportResult.ExportTransaction.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
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

  // TODO create parametrized tests taking class name and checking those values, for new class just add it to list and done :)
  // TODO correct all existing code to use such generic testing class - thanks to that Lombok generates and this class tests
  // TODO try someting difficult and write annotation processor which is finding those classes based on Lombok annotation (no need to register)

  @Test
  public void equalsContractExportPeriod() {
    EqualsVerifier.forClass(ExportResult.ExportPeriod.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

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

  @Test
  public void equalsContractExportResult() {
    EqualsVerifier.forClass(ExportResult.class)
        .withPrefabValues(Category.class, Category.builder().name("A").build(), Category.builder().name("B").build())
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

}
