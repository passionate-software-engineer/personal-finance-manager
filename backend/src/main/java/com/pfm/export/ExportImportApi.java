package com.pfm.export;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin
@Api(tags = {"export-import-controller"})
public interface ExportImportApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Export user data in JSON format", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
  })
  @GetMapping("export")
  ExportResult exportData();

  @ApiOperation(value = "Imports previously exported user data", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PostMapping("import")
  ResponseEntity<?> importData(ExportResult inputData);

}
