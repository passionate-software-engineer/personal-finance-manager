package com.pfm.export;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin
public interface ExportImportApi {

  @GetMapping("export")
  ExportResult exportData(long userId);

  @PostMapping("import")
  void importData(ExportResult inputData, long userId);

}
