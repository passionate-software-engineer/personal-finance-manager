package com.pfm.filter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Filters", description = "Controller used to list / add / update / delete filters.")
public interface FilterApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Find filter by id", response = Filter.class, authorizations = {@Authorization(value = BEARER)})
  @GetMapping(value = "/{filterId}")
  ResponseEntity<Filter> getFilterById(@PathVariable long filterId);

  @ApiOperation(value = "Get list of all filters", response = Filter.class, responseContainer = "List",
      authorizations = {@Authorization(value = BEARER)})
  @GetMapping
  ResponseEntity<List<Filter>> getFilters();

  @ApiOperation(value = "Create new filter", response = Long.class, authorizations = {@Authorization(value = BEARER)})
  @PostMapping
  ResponseEntity<?> addFilter(FilterRequest filterRequest);

  @ApiOperation(value = "Update an existing filter", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @PutMapping(value = "/{filterId}")
  ResponseEntity<?> updateFilter(@PathVariable long filterId, FilterRequest filterRequest);

  @ApiOperation(value = "Delete an existing filter", response = Void.class, authorizations = {@Authorization(value = BEARER)})
  @DeleteMapping(value = "/{filterId}")
  ResponseEntity<?> deleteFilter(@PathVariable long filterId);
}
