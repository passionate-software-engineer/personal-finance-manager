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

  private CategoryService categoryService;
  private AccountService accountService;

  public List<String> validateFilterRequest(Filter filter, long userId) {
    List<String> validationResults = new ArrayList<>();

    if (filter.getName() == null || filter.getName().trim().equals("")) {
      validationResults.add(getMessage(FILTER_EMPTY_NAME));
    }

    if (filter.getAccountIds() != null) {
      for (long id : filter.getAccountIds()) {
        if (accountService.accountDoesNotExistByIdAndUserId(id, userId)) {
          validationResults.add(String.format(getMessage(FILTER_ACCOUNT_ID_DOES_NOT_EXIST), id));
        }
      }
    }

    if (filter.getCategoryIds() != null) {
      for (long id : filter.getCategoryIds()) {
        if (!categoryService.categoryExistByIdAndUserId(id, userId)) {
          validationResults.add(String.format(getMessage(FILTER_CATEGORY_ID_DOES_NOT_EXIST), id));
        }
      }
    }

    if (filter.getPriceFrom() != null && filter.getPriceTo() != null
        && filter.getPriceFrom().compareTo(filter.getPriceTo()) > 0) {
      validationResults.add(getMessage(FILTER_PRICE_FROM_BIGGER_THEN_PRICE_TO));
    }

    if (filter.getDateFrom() != null && filter.getDateTo() != null
        && filter.getDateFrom().isAfter(filter.getDateTo())) {
      validationResults.add(getMessage(FILTER_DATE_FROM_IS_AFTER_DATE_TO));
    }

    return validationResults;
  }
}
