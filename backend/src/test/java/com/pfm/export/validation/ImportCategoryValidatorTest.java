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

        Arguments.arguments(missingPriority(),
            Collections.singletonList("CategoryName category has missing priority")),

        Arguments.arguments(onlyName(),
            Arrays.asList("CategoryName category has missing parent category name",
                "CategoryName category has missing priority")),

        Arguments.arguments(missingAllData(),
            Collections.singletonList("Category name is missing"))

    );
  }

  private static ExportResult.ExportCategory missingName() {
    return ExportCategory.builder()
        .parentCategoryName("ParentCategoryName")
        .priority(100)
        .build();
  }

  private static ExportResult.ExportCategory missingParentCategoryName() {
    return ExportCategory.builder()
        .name("CategoryName")
        .priority(100)
        .build();
  }

  private static ExportResult.ExportCategory missingPriority() {
    return ExportCategory.builder()
        .name("CategoryName")
        .parentCategoryName("ParentCategoryName")
        .build();
  }

  private static ExportResult.ExportCategory missingAllData() {
    return ExportCategory.builder()
        .build();
  }

  private static ExportResult.ExportCategory onlyName() {
    return ExportCategory.builder()
        .name("CategoryName")
        .build();
  }
}
