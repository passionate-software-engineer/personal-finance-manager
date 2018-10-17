package com.pfm.transaction;

import static com.pfm.test.helpers.TestCategoryProvider.categoryCar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceIntegrationTest {

  @SpyBean
  private TransactionService transactionService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private CategoryService categoryService;

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

  @Test
  public void shouldRollbackAccountStateUpdateWhenDeleteFailsBecauseOfInvalidData() {
    // given
    Account savedAccount = accountService.addAccount(Account.builder()
        .balance(BigDecimal.TEN)
        .name("mBank")
        .build()
    );

    long categoryId = categoryService.addCategory(categoryCar()).getId();

    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.ONE)
        .build())
    );
    transaction.setCategoryId(categoryId);
    transaction.setDescription("Transaction with price 1");
    transaction.setDate(LocalDate.now());

    transactionService.addTransaction(transaction);
    Mockito.when(transactionService.getTransactionById(42)).thenReturn(Optional.of(transaction));
    // when

    try {
      transactionService.deleteTransaction(42);
      fail();
    } catch (EmptyResultDataAccessException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountById(savedAccount.getId());
    assertThat(returnedAccount.isPresent(), is(true));

    Account account = returnedAccount.get();
    assertThat(returnedAccount.get().getBalance(), is(BigDecimal.valueOf(1100, 2)));
  }
  
}
