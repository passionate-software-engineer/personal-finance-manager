package com.pfm.export;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin
public interface ExportImportApi {

  @ApiOperation(value = "Export user data in JSON format", authorizations = {@Authorization(value = "Bearer")})
  @GetMapping("export")
  ExportResult exportData();

  @ApiOperation(value = "Imports previously exported user data", authorizations = {@Authorization(value = "Bearer")})
  @PostMapping("import")
  ResponseEntity<?> importData(ExportResult inputData);

}
