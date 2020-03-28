package com.pfm.auth;

import com.pfm.account.type.AccountTypeService;
import com.pfm.currency.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserInitializationService {

  private CurrencyService currencyService;
  private AccountTypeService accountTypeService;

  public void initializeUser(long userId) {
    currencyService.addDefaultCurrencies(userId);
    accountTypeService.addDefaultAccountTypes(userId);
  }

}
