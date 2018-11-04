package com.pfm.filter;

import com.pfm.history.HistoryEntryService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class FilterController implements FilterApi {

  private FilterService filterService;
  private FilterValidator filterValidator;
  private HistoryEntryService historyEntryService;

  @Override
  public ResponseEntity<Filter> getFilterById(@PathVariable long filterId, @RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving filter with id: {}", filterId);
    Optional<Filter> filter = filterService.getFilterByIdAndUserId(filterId, userId);

    if (!filter.isPresent()) {
      log.info("Filter with id {} was not found", filterId);
      return ResponseEntity.notFound().build();
    }

    log.info("Filter with id {} was successfully retrieved", filterId);
    return ResponseEntity.ok(filter.get());
  }

  @Override
  public ResponseEntity<List<Filter>> getFilters(@RequestAttribute(value = "userId") long userId) {
    return ResponseEntity.ok(filterService.getAllFilters(userId));
  }

  @Override
  @Transactional
  public ResponseEntity<?> addFilter(@RequestBody FilterRequest filterRequest, @RequestAttribute(value = "userId") long userId) {
    log.info("Adding filter to the database");

    Filter filter = convertFilterRequestToFilter(filterRequest);

    List<String> validationResult = filterValidator.validateFilterRequest(filter, userId);
    if (!validationResult.isEmpty()) {
      log.info("Filter is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Filter createdFilter = filterService.addFilter(userId, filter);
    log.info("Saving filter to the database was successful. Filter id is {}", createdFilter.getId());

    historyEntryService.addEntryOnAdd(filter, userId);

    return ResponseEntity.ok(createdFilter.getId());
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateFilter(@PathVariable long filterId, @RequestBody FilterRequest filterRequest,
      @RequestAttribute(value = "userId") long userId) {

    Optional<Filter> filterByIdAndUserId = filterService.getFilterByIdAndUserId(filterId, userId);
    if (!filterByIdAndUserId.isPresent()) {
      log.info("No filter with id {} was found, not able to update", filterId);
      return ResponseEntity.notFound().build();
    }

    Filter filter = convertFilterRequestToFilter(filterRequest);

    List<String> validationResult = filterValidator.validateFilterRequest(filter, userId);
    if (!validationResult.isEmpty()) {
      log.error("Filter is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }
    historyEntryService.addEntryOnUpdate(filterByIdAndUserId.get(), filter, userId);

    filterService.updateFilter(filterId, userId, filter);
    log.info("Filter with id {} was successfully updated", filterId);

    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteFilter(@PathVariable long filterId, @RequestAttribute(value = "userId") long userId) {
    Optional<Filter> filterByIdAndUserId = filterService.getFilterByIdAndUserId(filterId, userId);

    if (!filterByIdAndUserId.isPresent()) {
      log.info("No filter with id {} was found, not able to delete", filterId);
      return ResponseEntity.notFound().build();
    }

    filterService.deleteFilter(filterId);

    historyEntryService.addEntryOnDelete(filterByIdAndUserId.get(), userId);

    return ResponseEntity.ok().build();
  }

  private static Filter convertFilterRequestToFilter(FilterRequest filterRequest) {
    return Filter.builder()
        .name(filterRequest.getName())
        .dateFrom(filterRequest.getDateFrom())
        .dateTo(filterRequest.getDateTo())
        .accountIds(filterRequest.getAccountIds())
        .categoryIds(filterRequest.getCategoryIds())
        .priceFrom(filterRequest.getPriceFrom())
        .priceTo(filterRequest.getPriceTo())
        .description(filterRequest.getDescription())
        .build();
  }
}
