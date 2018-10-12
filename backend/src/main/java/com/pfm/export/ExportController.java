package com.pfm.export;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class ExportController implements ExportApi {

  private ExportService exportService;

  @Override
  public ExportResult exportData() {
    return exportService.exportData();
  }

  @Override
  public void importData(@RequestBody ExportResult inputData) {
    exportService.importData(inputData);
  }

}
