package com.pfm.account.performance;

import com.pfm.account.Account;
import org.junit.jupiter.api.Test;

public class AddAccountTest extends InvoicePerformanceTestBase {

  @Test
  //  @ThreadCount(THREAD_COUNT) // TODO add wrapper running tests in multiple tests
  public void shouldAddSimultaneouslyMultipleAccounts() {

    for (int i = 0; i < 10; ++i) {

      Account account = addAndReturnAccount();

      accounts.add(account);

    }
  }

}