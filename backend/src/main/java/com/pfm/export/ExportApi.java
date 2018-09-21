package com.pfm.export;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("export")
@CrossOrigin
public interface ExportApi {

  @GetMapping
  List<ExportPeriod> exportData();

}
