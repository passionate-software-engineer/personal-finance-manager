package com.pfm.transaction.import1;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@AllArgsConstructor
@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
public class FileHelper {

  private UniversalDetector encodingDetector;

  public File convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException {
    File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename(), "multipartFile cannot be null"));
    file.createNewFile();
    try (OutputStream outputStream = Files.newOutputStream(Paths.get(file.getPath()))) {
      outputStream.write(multipartFile.getBytes());
    }
    return file;
  }

  public String getFileEncoding(File file) throws IOException {
    String encoding;
    try (InputStream inputStream = Files.newInputStream(Paths.get(file.getPath()))) {
      byte[] bytes = Files.readAllBytes(file.toPath());
      int bytesReadCount = inputStream.read(bytes);
      if (bytesReadCount > 0 && !encodingDetector.isDone()) {
        encodingDetector.handleData(bytes, 0, bytesReadCount);
      }
      encodingDetector.dataEnd();
      encoding = encodingDetector.getDetectedCharset();
      if (encoding != null) {
        log.info("Detected encoding {} in {}", encoding, file.getName());
      } else {
        log.warn("No encoding detected in {}", file.getName());
      }
      encodingDetector.reset();
    }
    return encoding;
  }
}
