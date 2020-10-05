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
        Arguments.arguments(missingAccounts(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 accounts;")),
        Arguments.arguments(missingCategories(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 categories;")),
        Arguments.arguments(missingDateFrom(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 date from;")),
        Arguments.arguments(missingDateTo(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 date to;")),
        Arguments.arguments(missingDescription(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 description;")),
        Arguments.arguments(missingPriceFrom(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 price from;")),
        Arguments.arguments(missingPriceTo(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 price to;")),
        Arguments.arguments(missingAllDate(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 name; accounts;"
                + " categories; date from; date to; description; price from; price to;")),
        Arguments.arguments(onlyName(),
            Collections.singletonList("All incorrect or missing fields in filters number: 0 accounts;"
                + " categories; date from; date to; description; price from; price to;"))

    );
  }

  private static ExportResult.ExportFilter missingName() {
    ExportResult.ExportFilter missingName = correctFilter();
    missingName.setName("");
    return missingName;
  }

  private static ExportResult.ExportFilter missingAccounts() {
    ExportResult.ExportFilter missingAccounts = correctFilter();
    missingAccounts.setAccounts(null);
    return missingAccounts;
  }

  private static ExportResult.ExportFilter missingCategories() {
    ExportResult.ExportFilter missingCategories = correctFilter();
    missingCategories.setCategories(null);
    return missingCategories;
  }

  private static ExportResult.ExportFilter missingDateFrom() {
    ExportResult.ExportFilter missingDateFrom = correctFilter();
    missingDateFrom.setDateFrom(null);
    return missingDateFrom;
  }

  private static ExportResult.ExportFilter missingDateTo() {
    ExportResult.ExportFilter missingDateTo = correctFilter();
    missingDateTo.setDateTo(null);
    return missingDateTo;
  }

  private static ExportResult.ExportFilter missingDescription() {
    ExportResult.ExportFilter missingDescription = correctFilter();
    missingDescription.setDescription("");
    return missingDescription;
  }

  private static ExportResult.ExportFilter missingPriceFrom() {
    ExportResult.ExportFilter missingPriceFrom = correctFilter();
    missingPriceFrom.setPriceFrom(null);
    return missingPriceFrom;
  }

  private static ExportResult.ExportFilter missingPriceTo() {
    ExportResult.ExportFilter missingPriceTo = correctFilter();
    missingPriceTo.setPriceTo(null);
    return missingPriceTo;
  }

  private static ExportResult.ExportFilter missingAllDate() {
    return new ExportResult.ExportFilter();
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
