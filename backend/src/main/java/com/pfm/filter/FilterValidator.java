package com.pfm.filter;


import static com.pfm.config.MessagesProvider.ACCOUNT_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.CATEGORY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FilterValidator {

  FilterService filterService;


  public List<String> validateFilterRequest(FilterRequest filterRequest) {
    List<String> validationResults = new ArrayList<>();

    if (filterRequest.getName() == null || filterRequest.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    for (long id : filterRequest.getAccountsIds()){
      if (!filterService.getFilterById(id).isPresent()){
        validationResults.add(getMessage(FILTER_ACCOUNT_ID_DOES_NOT_EXIST));
      }
    }

    for (long id : filterRequest.getCategoryIds()){
      if (!filterService.getFilterById(id).isPresent()){
        validationResults.add(getMessage(CATEGORY_ID_DOES_NOT_EXIST));
      }
    }

    return validationResults;
  }
}
