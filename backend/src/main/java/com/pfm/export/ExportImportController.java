package com.pfm.export;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class ExportImportController implements ExportImportApi {

  private ExportService exportService;
  private ImportService importService;

  @Override
  public ExportResult exportData(@RequestAttribute(value = "userId") long userId) {
    return exportService.exportData(userId);
  }

  @Override
  public void importData(@RequestBody ExportResult inputData, @RequestAttribute(value = "userId") long userId) {
    // TODO [enhancement] validate input - if all required fields are present, check if no data is present before import
    importService.importData(inputData, userId);
  }

}
