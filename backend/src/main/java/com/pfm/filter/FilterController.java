package com.pfm.filter;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class FilterController implements FilterApi {

  private FilterService filterService;
  private FilterValidator filterValidator;
  private UserProvider userProvider;
  private HistoryEntryService historyEntryService;

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
        .isDefault(filterRequest.getIsDefault())
        .build();
  }

  @Override
  public ResponseEntity<Filter> getFilterById(@PathVariable long filterId) {
    long userId = userProvider.getCurrentUserId();

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
  public ResponseEntity<List<Filter>> getFilters() {
    long userId = userProvider.getCurrentUserId();

    return ResponseEntity.ok(filterService.getAllFilters(userId));
  }

  @Override
  @Transactional
  public ResponseEntity<?> addFilter(@RequestBody FilterRequest filterRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Adding filter to the database");

    Filter filter = convertFilterRequestToFilter(filterRequest);

    List<String> validationResult = filterValidator.validateFilterRequest(filter, userId);
    if (!validationResult.isEmpty()) {
      log.info("Filter is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Filter createdFilter = filterService.addFilter(userId, filter);
    log.info("Saving filter to the database was successful. Filter id is {}", createdFilter.getId());

    historyEntryService.addHistoryEntryOnAdd(createdFilter, userId);

    return ResponseEntity.ok(createdFilter.getId());
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateFilter(@PathVariable long filterId, @RequestBody FilterRequest filterRequest) {
    long userId = userProvider.getCurrentUserId();

    Optional<Filter> filterByIdAndUserId = filterService.getFilterByIdAndUserId(filterId, userId);
    if (!filterByIdAndUserId.isPresent()) {
      log.info("No filter with id {} was found, not able to update", filterId);
      return ResponseEntity.notFound().build();
    }

    Filter filter = convertFilterRequestToFilter(filterRequest);

    List<String> validationResult = filterValidator.validateFilterRequest(filter, userId);
    if (!validationResult.isEmpty()) {
      log.info("Filter is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }
    historyEntryService.addHistoryEntryOnUpdate(filterByIdAndUserId.get(), filter, userId);

    filterService.updateFilter(filterId, userId, filter);
    log.info("Filter with id {} was successfully updated", filterId);

    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteFilter(@PathVariable long filterId) {
    long userId = userProvider.getCurrentUserId();

    Optional<Filter> filterByIdAndUserId = filterService.getFilterByIdAndUserId(filterId, userId);
    if (!filterByIdAndUserId.isPresent()) {
      log.info("No filter with id {} was found, not able to delete", filterId);
      return ResponseEntity.notFound().build();
    }

    historyEntryService.addHistoryEntryOnDelete(filterByIdAndUserId.get(), userId);

    filterService.deleteFilter(filterId);

    return ResponseEntity.ok().build();
  }
}
