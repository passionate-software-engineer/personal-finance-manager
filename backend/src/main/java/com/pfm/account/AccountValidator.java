package com.pfm.account;

import com.pfm.config.ResourceBundleConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountValidator {

  private ResourceBundleConfig resourceBundleConfig;

  public List<String> validate(Account account) {
    List<String> validationErrors = new ArrayList<>();

    if (account.getName() == null || account.getName().trim().equals("")) {
      validationErrors.add(resourceBundleConfig.getMessage("emptyAccountName"));
    }

    if (account.getBalance() == null) {
      validationErrors.add(resourceBundleConfig.getMessage("emptyAccountBalance"));
    }
    return validationErrors;
  }

}