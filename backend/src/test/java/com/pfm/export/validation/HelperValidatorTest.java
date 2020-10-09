package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HelperValidatorTest {

  private final HelperValidator helperValidator = new HelperValidator();

  @Test
  void shouldCorrectCheckData() {
    // given
    final String correct = "similar";
    final String blank = "    ";
    final String empty = "";
    final String nullString = null;

    // when
    assertFalse(helperValidator.isDataIncorrect(correct));
    assertTrue(helperValidator.isDataIncorrect(blank));
    assertTrue(helperValidator.isDataIncorrect(empty));
    assertTrue(helperValidator.isDataIncorrect(nullString));
  }

  @Test
  void shouldCreateResultMessage() {
    // given
    final String expectedMessage = "All incorrect or missing fields in category number: 0 name;";
    final String placeName = "category";
    final int numberInRow = 0;
    final String missingField = " name;";

    // then
    String result = helperValidator.createResultMessage(placeName, numberInRow, missingField);

    // when
    assertEquals(expectedMessage, result);
  }
}
