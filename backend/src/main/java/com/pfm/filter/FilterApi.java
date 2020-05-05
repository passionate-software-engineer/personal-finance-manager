package com.pfm.filter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
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
@Api(tags = {"filter-controller"})
public interface FilterApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Find filter by id", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Filter.class),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
  })
  @GetMapping(value = "/{filterId}")
  ResponseEntity<Filter> getFilterById(@PathVariable long filterId);

  @ApiOperation(value = "Get list of all filters", response = Filter.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Filter.class, responseContainer = "list"),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
  })
  @GetMapping
  ResponseEntity<List<Filter>> getFilters();

  @ApiOperation(value = "Create new filter", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Long.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PostMapping
  ResponseEntity<?> addFilter(FilterRequest filterRequest);

  @ApiOperation(value = "Update an existing filter", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PutMapping(value = "/{filterId}")
  ResponseEntity<?> updateFilter(@PathVariable long filterId, FilterRequest filterRequest);

  @ApiOperation(value = "Delete an existing filter", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Void.class),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
  })
  @DeleteMapping(value = "/{filterId}")
  ResponseEntity<?> deleteFilter(@PathVariable long filterId);
}
