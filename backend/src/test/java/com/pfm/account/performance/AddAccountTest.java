package com.pfm.account.performance;

import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.pfm.account.Account;
import org.junit.Test;

public class AddAccountTest extends InvoicePerformanceTestBase {

  @Test
  @ThreadCount(THREAD_COUNT)
  public void shouldAddSimultaneouslyMultipleAccounts() {

    for (int i = 0; i < 10; ++i) {

      Account account = getAccount();

      accounts.add(account);

    }
  }

}