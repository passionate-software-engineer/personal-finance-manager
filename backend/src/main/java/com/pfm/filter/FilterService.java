package com.pfm.filter;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FilterService {

  private FilterRepository filterRepository;
  private AccountService accountService;
  private CategoryService categoryService;

  public Filter addFilter(FilterRequest filterRequest) {

    Filter filter = Filter.builder()
        .accounts(
            filterRequest.getAccountsIds().stream().map(accountId -> accountService.getAccountById(accountId).get()).collect(Collectors.toList()))
        .categories(
            filterRequest.getCategoryIds().stream().map(categoryId -> categoryService.getCategoryById(categoryId).get()).collect(Collectors.toList()))
        .name(filterRequest.getName())
        .description(filterRequest.getDescription())
        .priceFrom(filterRequest.getPriceFrom())
        .priceTo(filterRequest.getPriceTo())
        .build();

    return filterRepository.save(filter);
  }

  public Filter getFilterById(long id) {
    return filterRepository.findById(id).get();
  }

}
