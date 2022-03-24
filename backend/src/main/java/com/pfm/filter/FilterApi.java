package com.pfm.filter;

import static com.pfm.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.pfm.helpers.http.HttpCodesAsString.BAD_REQUEST;
import static com.pfm.helpers.http.HttpCodesAsString.NOT_FOUND;
import static com.pfm.helpers.http.HttpCodesAsString.OK;
import static com.pfm.helpers.http.HttpCodesAsString.UNAUTHORIZED;
import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.NOT_FOUND_MESSAGE;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("filters")
@CrossOrigin
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@Tag(name = "Filter Controller", description = "Controller used to list / add / update / delete filters.")
public interface FilterApi {

  @Operation(summary = "Find filter by id")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = Filter.class))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @GetMapping(value = "/{filterId}")
  ResponseEntity<Filter> getFilterById(@PathVariable long filterId);

  @Operation(summary = "Get list of all filters")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = Filter.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @GetMapping
  ResponseEntity<List<Filter>> getFilters();

  @Operation(summary = "Create new filter")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PostMapping
  ResponseEntity<?> addFilter(FilterRequest filterRequest);

  @Operation(summary = "Update an existing filter")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PutMapping(value = "/{filterId}")
  ResponseEntity<?> updateFilter(@PathVariable long filterId, FilterRequest filterRequest);

  @Operation(summary = "Delete an existing filter")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @DeleteMapping(value = "/{filterId}")
  ResponseEntity<?> deleteFilter(@PathVariable long filterId);
}
