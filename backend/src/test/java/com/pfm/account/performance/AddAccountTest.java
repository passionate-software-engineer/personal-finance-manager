package com.pfm.account.performance;

import com.pfm.account.Account;
import com.pfm.account.type.AccountType;
import com.pfm.currency.Currency;
import org.junit.jupiter.api.Test;

public class AddAccountTest extends InvoicePerformanceTestBase {

  @Test
  public void shouldAddSimultaneouslyMultipleAccounts() throws InterruptedException {
    runInMultipleThreads(() -> {

      Currency[] currencies = getCurrencies();
      AccountType[] accountType = getAccountTypes();

      for (int i = 0; i < 10; ++i) {

        Account account = addAndReturnAccount(currencies, accountType);

        accounts.add(account);

      }

    });
  }

}
