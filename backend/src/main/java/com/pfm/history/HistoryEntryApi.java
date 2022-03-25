package com.pfm.history;

import static com.pfm.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.pfm.helpers.http.HttpCodesAsString.OK;
import static com.pfm.helpers.http.HttpCodesAsString.UNAUTHORIZED;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;
import static com.pfm.swagger.ApiConstants.UNAUTHORIZED_MESSAGE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("history")
@CrossOrigin
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@Tag(name = "History Entry Controller", description = "Controller used to list history.")
public interface HistoryEntryApi {

  // TODO add option to return history entries by date range
  @Operation(summary = "List history entries")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = HistoryEntry.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @GetMapping
  ResponseEntity<List<HistoryEntry>> getHistory();

}
