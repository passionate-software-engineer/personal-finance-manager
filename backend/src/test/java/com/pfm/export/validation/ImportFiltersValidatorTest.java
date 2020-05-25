package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportFilter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportFiltersValidatorTest {

  private ImportFiltersValidator importFiltersValidator = new ImportFiltersValidator();

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

  static Stream<Arguments> filtersValidate() {
    return Stream.of(

        Arguments.arguments(missingName(),
            Collections.singletonList("Filter name is missing")),

        Arguments.arguments(missingAccounts(),
            Collections.singletonList("ExampleFilterName filter has missing accounts")),

        Arguments.arguments(missingCategories(),
            Collections.singletonList("ExampleFilterName filter has missing categories")),

        Arguments.arguments(missingDateFrom(),
            Collections.singletonList("ExampleFilterName filter has missing date from")),

        Arguments.arguments(missingDateTo(),
            Collections.singletonList("ExampleFilterName filter has missing date to")),

        Arguments.arguments(missingDescription(),
            Collections.singletonList("ExampleFilterName filter has missing description")),

        Arguments.arguments(missingPriceFrom(),
            Collections.singletonList("ExampleFilterName filter has missing price from")),

        Arguments.arguments(missingPriceTo(),
            Collections.singletonList("ExampleFilterName filter has missing price to")),

        Arguments.arguments(missingAllDate(),
            Collections.singletonList("Filter name is missing")),

        Arguments.arguments(onlyName(),
            Arrays.asList("ExampleFilterName filter has missing accounts",
                "ExampleFilterName filter has missing categories",
                "ExampleFilterName filter has missing date from",
                "ExampleFilterName filter has missing date to",
                "ExampleFilterName filter has missing description",
                "ExampleFilterName filter has missing price from",
                "ExampleFilterName filter has missing price to"))

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
}
