package com.pfm.export;

import static com.pfm.swagger.ApiConstants.BEARER;
import static com.pfm.swagger.ApiConstants.CONTAINER_LIST;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;
import static com.pfm.swagger.ApiConstants.UNAUTHORIZED_MESSAGE;

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

  @ApiOperation(value = "Export user data in JSON format", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @GetMapping("export")
  ExportResult exportData();

  @ApiOperation(value = "Imports previously exported user data", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
      @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = String.class),
  })
  @PostMapping("import")
  ResponseEntity<?> importData(ExportResult inputData);

}
