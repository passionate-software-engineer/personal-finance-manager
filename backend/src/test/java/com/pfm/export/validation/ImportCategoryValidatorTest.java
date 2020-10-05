package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportCategory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportCategoryValidatorTest {

  private ImportCategoryValidator importCategoryValidator;

  static Stream<Arguments> categoryValidate() {
    return Stream.of(
        Arguments.arguments(missingName(),
            Collections.singletonList("All incorrect or missing fields in category number: 0 name;")),
        Arguments.arguments(incorrectPriority(),
            Collections.singletonList("All incorrect or missing fields in category number: 0 priority;")),
        Arguments.arguments(onlyName(),
            Collections.singletonList("All incorrect or missing fields in category number: 0 parent category name; priority;")),
        Arguments.arguments(missingAllData(),
            Collections.singletonList("All incorrect or missing fields in category number: 0 name; priority;"))
    );
  }

  private static ExportResult.ExportCategory missingName() {
    ExportCategory exportCategory = correctCategory();
    exportCategory.setName("");
    return exportCategory;
  }

  private static ExportResult.ExportCategory incorrectPriority() {
    ExportCategory exportCategory = correctCategory();
    exportCategory.setPriority(9999999);
    return exportCategory;
  }

  private static ExportResult.ExportCategory missingAllData() {
    return new ExportCategory();
  }

  private static ExportResult.ExportCategory onlyName() {
    return ExportCategory.builder()
        .name("CategoryName")
        .build();
  }

  private static ExportResult.ExportCategory correctCategory() {
    return ExportCategory.builder()
        .name("CategoryName")
        .parentCategoryName("ParentCategoryName")
        .priority(100)
        .build();
  }

  @BeforeEach
  void setUp() {
    importCategoryValidator = new ImportCategoryValidator();
  }

  @ParameterizedTest
  @MethodSource("categoryValidate")
  public void shouldReturnErrorLogForMissingData(ExportCategory inputCategory, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setCategories(Collections.singletonList(inputCategory));

    // when
    List<String> result = importCategoryValidator.validate(input.getCategories());

    // then
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }
}
