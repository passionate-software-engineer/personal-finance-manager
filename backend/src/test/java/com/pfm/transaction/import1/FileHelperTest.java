package com.pfm.transaction.import1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT"})
class FileHelperTest {

  @SpyBean
  UniversalDetector encodingDetector;

  @InjectMocks
  FileHelper fileHelper;

  @Test
  void shouldReturnNullForNotDetectedFileEncoding() throws IOException {
    // given
    final String path = "src/test/resources/csv/ing/original.csv";
    File file = new File(path);
    // we stub isDone method, but encoding detector did not even get chance to read the file, so encoding was not detected
    doReturn(true).when(encodingDetector).isDone();

    // when
    final String detectedCharset = fileHelper.getFileEncoding(file);

    // then
    verify(encodingDetector, times(1)).isDone();
    assertThat(detectedCharset, is(equalTo(null)));
  }
}
