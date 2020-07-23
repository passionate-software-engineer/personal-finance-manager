package com.pfm.transaction.import1;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvParserTest {

  @InjectMocks
  private CsvParser csvParser;

  @Test
  void shouldThrowIoExceptionWhenParserThrowsIoException() throws IOException {
    // given
    File file = new File("NotExistingPath");

    // then
    assertThrows(TransactionsParsingException.class, () -> csvParser.parseBeans(file));
  }
}
