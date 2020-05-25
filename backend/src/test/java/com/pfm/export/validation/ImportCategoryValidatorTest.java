package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportCategory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportCategoryValidatorTest {

  private ImportCategoryValidator importCategoryValidator = new ImportCategoryValidator();

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

  static Stream<Arguments> categoryValidate() {
    return Stream.of(

        Arguments.arguments(missingName(),
            Collections.singletonList("Category name is missing")),

        Arguments.arguments(missingParentCategoryName(),
            Collections.singletonList("CategoryName category has missing parent category name")),

        Arguments.arguments(incorrectPriority(),
            Collections.singletonList("CategoryName category has incorrect priority")),

        Arguments.arguments(onlyName(),
            Arrays.asList("CategoryName category has missing parent category name",
                "CategoryName category has incorrect priority")),

        Arguments.arguments(missingAllData(),
            Collections.singletonList("Category name is missing"))

    );
  }

  private static ExportResult.ExportCategory missingName() {
    ExportCategory exportCategory = correctCategory();
    exportCategory.setName("");
    return exportCategory;
  }

  private static ExportResult.ExportCategory missingParentCategoryName() {
    ExportCategory exportCategory = correctCategory();
    exportCategory.setParentCategoryName("");
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
}
