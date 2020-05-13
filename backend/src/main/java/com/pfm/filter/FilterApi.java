package com.pfm.filter;

import com.pfm.swagger.ApiConstants;
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

  @ApiOperation(value = "Find filter by id", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Filter.class),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = ApiConstants._404_NOT_FOUND_MESSAGE),
  })
  @GetMapping(value = "/{filterId}")
  ResponseEntity<Filter> getFilterById(@PathVariable long filterId);

  @ApiOperation(value = "Get list of all filters", response = Filter.class, authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Filter.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @GetMapping
  ResponseEntity<List<Filter>> getFilters();

  @ApiOperation(value = "Create new filter", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = ApiConstants._400_BAD_REQ_MESSAGE, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addFilter(FilterRequest filterRequest);

  @ApiOperation(value = "Update an existing filter", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE),
      @ApiResponse(code = 400, message = ApiConstants._400_BAD_REQ_MESSAGE, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @PutMapping(value = "/{filterId}")
  ResponseEntity<?> updateFilter(@PathVariable long filterId, FilterRequest filterRequest);

  @ApiOperation(value = "Delete an existing filter", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = ApiConstants._404_NOT_FOUND_MESSAGE),
  })
  @DeleteMapping(value = "/{filterId}")
  ResponseEntity<?> deleteFilter(@PathVariable long filterId);
}
