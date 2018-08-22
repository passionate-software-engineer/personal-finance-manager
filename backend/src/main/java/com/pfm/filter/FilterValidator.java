package com.pfm.filter;


import static com.pfm.config.MessagesProvider.FILTER_ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.FILTER_CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.FILTER_DATE_FROM_IS_AFTER_DATE_TO;
import static com.pfm.config.MessagesProvider.FILTER_EMPTY_NAME;
import static com.pfm.config.MessagesProvider.FILTER_PRICE_FROM_BIGGER_THEN_PRICE_TO;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FilterValidator {

  private FilterService filterService;
  private CategoryService categoryService;
  private AccountService accountService;


  public List<String> validateFilterRequest(FilterRequest filterRequest) {
    List<String> validationResults = new ArrayList<>();

    if (filterRequest.getName() == null || filterRequest.getName().trim().equals("")) {
      validationResults.add(getMessage(FILTER_EMPTY_NAME));
    }

    for (long id : filterRequest.getAccountsIds()) {
      if (!accountService.getAccountById(id).isPresent()) {
        validationResults.add(getMessage(FILTER_ACCOUNT_ID_DOES_NOT_EXIST) + id);
      }
    }

    for (long id : filterRequest.getCategoryIds()) {
      if (!categoryService.getCategoryById(id).isPresent()) {
        validationResults.add(getMessage(FILTER_CATEGORY_ID_DOES_NOT_EXIST) + id);
      }
    }

    if (filterRequest.getPriceFrom() != null && filterRequest.getPriceTo() != null &&
        filterRequest.getPriceFrom().compareTo(filterRequest.getPriceTo()) > 0) {
      validationResults.add(getMessage(FILTER_PRICE_FROM_BIGGER_THEN_PRICE_TO));
    }

    if (filterRequest.getDateFrom() != null & filterRequest.getDateTo() != null && filterRequest.getDateFrom().isAfter(filterRequest.getDateTo())) {
      validationResults.add(getMessage(FILTER_DATE_FROM_IS_AFTER_DATE_TO));
    }

    return validationResults;
  }
}
