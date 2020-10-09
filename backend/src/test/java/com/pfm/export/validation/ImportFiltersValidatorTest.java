package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportFilter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportFiltersValidatorTest {

  private ImportFiltersValidator importFiltersValidator;

  static Stream<Arguments> filtersValidate() {
    return Stream.of(

        Arguments.arguments(missingName(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 name;")),
        Arguments.arguments(onlyName(),
            Collections.emptyList())
    );
  }

  private static ExportResult.ExportFilter missingName() {
    ExportResult.ExportFilter missingName = correctFilter();
    missingName.setName("");
    return missingName;
  }

  private static ExportResult.ExportFilter onlyName() {
    ExportResult.ExportFilter onlyName = new ExportFilter();
    onlyName.setName("ExampleFilterName");
    return onlyName;
  }

  private static ExportResult.ExportFilter correctFilter() {
    return ExportFilter.builder()
        .name("ExampleFilterName")
        .accounts(Collections.singletonList("Example Account"))
        .categories(Collections.singletonList("Example Category"))
        .dateFrom(LocalDate.now())
        .dateTo(LocalDate.now())
        .description("Example description")
        .priceFrom(BigDecimal.valueOf(10))
        .priceTo(BigDecimal.valueOf(100))
        .build();
  }

  @BeforeEach
  void setUp() {
    importFiltersValidator = new ImportFiltersValidator();
  }

  @ParameterizedTest
  @MethodSource("filtersValidate")
  public void shouldReturnErrorLogForMissingData(ExportFilter inputFilter, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setFilters(Collections.singletonList(inputFilter));

    // when
    List<String> result = importFiltersValidator.validate(input.getFilters());

    // then
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }
}
