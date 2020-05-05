package com.pfm.export.converter;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FilterExporter {

  private final FilterService filterService;
  private final AccountService accountService;
  private final CategoryService categoryService;

  // TODO: Instead of multiple queries (needed to fetch categories, accounts) just write some SQL and
  //  fetch it in one query with jdbcTemplate.
  public List<ExportFilter> export(long userId) {
    return filterService.getAllFilters(userId)
        .stream()
        .map(filter -> mapToExportFilter(userId, filter))
        .collect(Collectors.toList());
  }

  private ExportFilter mapToExportFilter(long userId, Filter filter) {
    return ExportFilter.builder()
        .name(filter.getName())
        .priceFrom(filter.getPriceFrom())
        .priceTo(filter.getPriceTo())
        .dateFrom(filter.getDateFrom())
        .dateTo(filter.getDateTo())
        .description(filter.getDescription())
        .accounts(mapAccounts(userId, filter))
        .categories(mapCategories(userId, filter))
        .build();
  }

  private List<String> mapCategories(long userId, Filter filter) {
    return filter.getCategoryIds().stream()
        .map(categoryId -> categoryService.getCategoryByIdAndUserId(categoryId, userId).orElse(new Category()).getName())
        .collect(Collectors.toList());
  }

  private List<String> mapAccounts(long userId, Filter filter) {
    return filter.getAccountIds().stream()
        .map(accountId -> accountService.getAccountByIdAndUserId(accountId, userId).orElse(new Account()).getName())
        .collect(Collectors.toList());
  }

}
