package com.pfm.filter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

  @ApiOperation(value = "Find filter by id", response = Filter.class)
  @GetMapping(value = "/{filterId}")
  ResponseEntity<Filter> getFilterById(@PathVariable long filterId, long userId);

  @ApiOperation(value = "Get list of all filters", response = Filter.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Filter>> getFilters(long userId);

  @ApiOperation(value = "Create new filter", response = Long.class)
  @PostMapping
  ResponseEntity<?> addFilter(FilterRequest filterRequest, long userId);

  @ApiOperation(value = "Update an existing filter", response = Void.class)
  @PutMapping(value = "/{filterId}")
  ResponseEntity<?> updateFilter(@PathVariable long filterId, FilterRequest filterRequest, long userId);

  @ApiOperation(value = "Delete an existing filter", response = Void.class)
  @DeleteMapping(value = "/{filterId}")
  ResponseEntity<?> deleteFilter(@PathVariable long filterId, long userId);
}
