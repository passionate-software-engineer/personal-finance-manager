package com.pfm.export.validation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.pfm.export.ExportResult;
import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryEntry.Type;
import com.pfm.history.HistoryInfo;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportHistoryEntriesValidatorTest {

  private ImportHistoryEntriesValidator importHistoryEntriesValidator;

  static Stream<Arguments> historyEntriesValidate() {
    return Stream.of(
        Arguments.arguments(missingParentDate(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0 date;")),
        Arguments.arguments(missingParentObject(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0 object;")),
        Arguments.arguments(missingParentType(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0 type;")),
        Arguments.arguments(missingChildName(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0 missing in entry number:0 name;")),
        Arguments.arguments(missingChildNewValue(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0 missing in entry number:0 new value;")),
        Arguments.arguments(onlyParentIdAndChildName(),
            Collections.singletonList("All incorrect or missing fields in history entries number: 0"
                + " date; object; type; missing in entry number:0 new value;"))
    );
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

  private static HistoryEntry missingChildNewValue() {
    HistoryEntry historyEntry = correctHistoryEntry();

    List<HistoryInfo> historyInfo = correctEntry();

    for (HistoryInfo historyInfo1 : historyInfo) {
      historyInfo1.setNewValue("");
    }

    historyEntry.setEntries(historyInfo);

    return historyEntry;
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

  @BeforeEach
  void setUp() {
    importHistoryEntriesValidator = new ImportHistoryEntriesValidator();
  }

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
}
