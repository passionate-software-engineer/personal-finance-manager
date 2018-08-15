package com.pfm.filter;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.List;
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
    return ResponseEntity.ok(filterService.getFilterById(id));
  }

  @Override
  public ResponseEntity<List<Filter>> getFilters() {
    return null;
  }

  @Override
  public ResponseEntity<?> addFilter(@RequestBody FilterRequest filterRequest) {
    long transactionId = filterService.addFilter(filterRequest).getId();
    return ResponseEntity.ok(transactionId);
  }

  @Override
  public ResponseEntity<?> updateFilter(long id, FilterRequest filterRequest) {
    return null;
  }

  @Override
  public ResponseEntity<?> deleteFilter(long id) {
    return null;
  }


}
