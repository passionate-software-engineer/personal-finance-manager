package com.pfm.filter;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.transaction.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class FilterController implements FilterApi {

  private FilterService filterService;
  private FilterValidator filterValidator;
  private AccountService accountService;
  private CategoryService categoryService;

  @Override
  public ResponseEntity<Filter> getFilterById(long id) {
    log.info("Retrieving filter with id: {}", id);
    Optional<Filter> filter = filterService.getFilterById(id);

    if (!filter.isPresent()) {
      log.info("Filter with id {} was not found", id);
      return ResponseEntity.notFound().build();
    }

    log.info("Filter with id {} was successfully retrieved", id);
    return ResponseEntity.ok(filter.get());
  }

  @Override
  public ResponseEntity<List<Filter>> getFilters() {
    return ResponseEntity.ok(filterService.getAllFilters());
  }

  @Override
  public ResponseEntity<?> addFilter(@RequestBody FilterRequest filterRequest) {
    log.info("Adding filter to the database");

    List<String> validationResult = filterValidator.validateFilterRequest(filterRequest);
    if (!validationResult.isEmpty()) {
      log.info("Filter is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Filter filter = convertFilterRequestToFilter(filterRequest);

    Filter createdFilter = filterService.addFilter(filter);
    log.info("Saving transaction to the database was successful. Transaction id is {}", createdFilter.getId());

    return ResponseEntity.ok(createdFilter.getId());
  }

  @Override
  public ResponseEntity<?> updateFilter(long id, FilterRequest filterRequest) {
    return null;
  }

  @Override
  public ResponseEntity<?> deleteFilter(long id) {
    if (!filterService.getFilterById(id).isPresent()) {
      log.info("No transaction with id {} was found, not able to delete", id);
      return ResponseEntity.notFound().build();
    }
    filterService.deleteFilter(id);
    return ResponseEntity.ok().build();
  }

  private Filter convertFilterRequestToFilter(FilterRequest filterRequest) {

    return Filter.builder()
        .name(filterRequest.getName())
        .dateFrom(filterRequest.getDateFrom())
        .dateTo(filterRequest.getDateTo())
        .accountsIds(filterRequest.getAccountsIds())
        .categoriesIds(filterRequest.getCategoryIds())
        .priceFrom(filterRequest.getPriceFrom())
        .priceTo(filterRequest.getPriceTo())
        .description(filterRequest.getDescription())
        .build();
  }
}
