package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryEntry.Type;
import com.pfm.history.HistoryInfo;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportHistoryEntriesValidatorTest {

  private ImportHistoryEntriesValidator importHistoryEntriesValidator = new ImportHistoryEntriesValidator();

  @ParameterizedTest
  @MethodSource("historyEntriesValidate")
  public void shouldReturnErrorLogForMissingData(HistoryEntry inputHistoryEntry, List<String> expectedMessages) {
    // given
    ExportResult input = new ExportResult();
    input.setHistoryEntries(Collections.singletonList(inputHistoryEntry));

    // when
    List<String> result = importHistoryEntriesValidator.validate(input.getHistoryEntries());

    // then
    assertArrayEquals(expectedMessages.toArray(), result.toArray());
  }

  static Stream<Arguments> historyEntriesValidate() {
    return Stream.of(
        Arguments.arguments(missingParentId(),
            Collections.singletonList("History entry ID is missing")),

        Arguments.arguments(missingParentDate(),
            Collections.singletonList("255 history entry ID has missing date")),

        Arguments.arguments(missingParentObject(),
            Collections.singletonList("255 history entry ID has missing object")),

        Arguments.arguments(missingParentType(),
            Collections.singletonList("255 history entry ID has missing type")),

        Arguments.arguments(missingChildName(),
            Collections.singletonList("Entry ID has missing name in history entry ID:255")),

        Arguments.arguments(missingChildId(),
            Collections.singletonList("Child entry has missing ID in history entry ID:255")),

        Arguments.arguments(missingChildNewValue(),
            Collections.singletonList("Child entry has missing new value in history entry ID:255")),

        Arguments.arguments(missingChildOldValue(),
            Collections.singletonList("Child entry has missing old value in history entry ID:255")),

        Arguments.arguments(missingAllData(),
            Collections.singletonList("History entry ID is missing")),

        Arguments.arguments(onlyParentIdAndChildName(),
            Arrays.asList("255 history entry ID has missing date",
                "Child entry has missing ID in history entry ID:255",
                "Child entry has missing new value in history entry ID:255",
                "Child entry has missing old value in history entry ID:255",
                "255 history entry ID has missing object",
                "255 history entry ID has missing type"))
    );
  }

  private static HistoryEntry missingParentId() {
    HistoryEntry historyEntry = correctHistoryEntry();
    historyEntry.setId(null);
    return historyEntry;
  }

  private static HistoryEntry missingParentDate() {
    HistoryEntry historyEntry = correctHistoryEntry();
    historyEntry.setDate(null);
    return historyEntry;
  }

  private static HistoryEntry missingParentObject() {
    HistoryEntry historyEntry = correctHistoryEntry();
    historyEntry.setObject(null);
    return historyEntry;
  }

  private static HistoryEntry missingParentType() {
    HistoryEntry historyEntry = correctHistoryEntry();
    historyEntry.setType(null);
    return historyEntry;
  }

  private static HistoryEntry missingChildName() {
    HistoryEntry historyEntry = correctHistoryEntry();

    List<HistoryInfo> historyInfo = correctEntry();

    for (HistoryInfo historyInfo1 : historyInfo) {
      historyInfo1.setName("");
    }

    historyEntry.setEntries(historyInfo);

    return historyEntry;
  }

  private static HistoryEntry missingChildId() {
    HistoryEntry historyEntry = correctHistoryEntry();

    List<HistoryInfo> historyInfo = correctEntry();

    for (HistoryInfo historyInfo1 : historyInfo) {
      historyInfo1.setId(null);
    }

    historyEntry.setEntries(historyInfo);

    return historyEntry;
  }

  private static HistoryEntry missingChildNewValue() {
    HistoryEntry historyEntry = correctHistoryEntry();

    List<HistoryInfo> historyInfo = correctEntry();

    for (HistoryInfo historyInfo1 : historyInfo) {
      historyInfo1.setNewValue("");
    }

    historyEntry.setEntries(historyInfo);

    return historyEntry;
  }

  private static HistoryEntry missingChildOldValue() {
    HistoryEntry historyEntry = correctHistoryEntry();

    List<HistoryInfo> historyInfo = correctEntry();

    for (HistoryInfo historyInfo1 : historyInfo) {
      historyInfo1.setOldValue("");
    }

    historyEntry.setEntries(historyInfo);

    return historyEntry;
  }

  private static HistoryEntry missingAllData() {
    return new HistoryEntry();
  }

  private static HistoryEntry onlyParentIdAndChildName() {
    HistoryEntry historyEntry = new HistoryEntry();
    historyEntry.setId(255L);

    HistoryInfo historyInfo = new HistoryInfo();
    historyInfo.setName("Child");

    historyEntry.setEntries(Collections.singletonList(historyInfo));
    return historyEntry;
  }

  private static HistoryEntry correctHistoryEntry() {
    return HistoryEntry.builder()
        .id(255L)
        .date(ZonedDateTime.now())
        .entries(correctEntry())
        .object("Object")
        .type(Type.ADD)
        .build();
  }

  private static List<HistoryInfo> correctEntry() {

    HistoryInfo historyInfo = new HistoryInfo();

    historyInfo.setName("Child");
    historyInfo.setId(127L);
    historyInfo.setNewValue("NewValue");
    historyInfo.setOldValue("OldValue");

    return Collections.singletonList(historyInfo);
  }
}
