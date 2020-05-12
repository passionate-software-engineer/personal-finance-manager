package com.pfm.export;

import com.pfm.swagger.ApiConstants;
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

  @ApiOperation(value = "Export user data in JSON format", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants.message1, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants.message3, response = String.class),
  })
  @GetMapping("export")
  ExportResult exportData();

  @ApiOperation(value = "Imports previously exported user data", authorizations = {@Authorization(value = "Bearer")})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants.message1, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants.message3, response = String.class),
  })
  @PostMapping("import")
  ResponseEntity<?> importData(ExportResult inputData);

}
