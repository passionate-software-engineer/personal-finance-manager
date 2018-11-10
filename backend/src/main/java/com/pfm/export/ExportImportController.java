package com.pfm.export;

import com.pfm.auth.UserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController // TODO add support for history - import data should be part of the history
public class ExportImportController implements ExportImportApi {

  private ExportService exportService;
  private ImportService importService;
  private UserProvider userProvider;

  @Override
  public ExportResult exportData() {
    long userId = userProvider.getCurrentUserId();

    return exportService.exportData(userId);
  }

  @Override
  public void importData(@RequestBody ExportResult inputData) {
    long userId = userProvider.getCurrentUserId();
    // TODO validate input - if all required fields are present, check if no data is present before import
    importService.importData(inputData, userId);
  }

}
