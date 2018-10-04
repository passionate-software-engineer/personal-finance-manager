package com.pfm.export;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("export")
@CrossOrigin
public interface ExportApi {

  @GetMapping
  ExportResult exportData();

  @PostMapping
  void importData(ExportResult inputData);

}
