package com.pfm.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceIntegrationTest {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private AccountService accountService;

  @Test
  public void shouldRollbackAccountStateUpdateWhenSaveFailsBecauseOfInvalidData() {
    // given
    Account savedAccount = accountService.addAccount(Account.builder()
        .balance(BigDecimal.TEN)
        .name("mBank")
        .build()
    );

    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.ONE)
        .build())
    );

    // when
    try {
      transactionService.addTransaction(transaction);
      fail();
    } catch (DataIntegrityViolationException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountById(savedAccount.getId());
    assertThat(returnedAccount.isPresent(), is(true));

    Account account = returnedAccount.get();
    assertThat(account.getBalance(), is(BigDecimal.valueOf(1000, 2)));
  }

}
