package com.pfm.transaction;

import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.helpers.TestUsersProvider.userMirek;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserService;
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

  @Autowired
  UserService userService;

  @Test
  public void shouldRollbackAccountStateUpdateWhenSaveFailsBecauseOfInvalidData() {

    // given
    long userId = userService.registerUser(userZdzislaw()).getId();

    Account savedAccount = accountService.addAccount(Account.builder()
        .balance(BigDecimal.TEN)
        .name("Idea bank")
        .userId(userId)
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
      transactionService.addTransaction(userId, transaction);
      fail();
    } catch (DataIntegrityViolationException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountByIdAndUserId(savedAccount.getId(), userId);
    assertThat(returnedAccount.isPresent(), is(true));

    Account account = returnedAccount.get();
    assertThat(account.getBalance(), is(BigDecimal.valueOf(1000, 2)));
  }

  @Test
  public void shouldRollbackAccountStateUpdateWhenDeleteFailsBecauseOfInvalidData() {

    // given
    long userId = userService.registerUser(userMarian()).getId();

    Account savedAccount = accountService.addAccount(Account.builder()
        .balance(BigDecimal.TEN)
        .name("mBank")
        .userId(userId)
        .build()
    );

    long categoryId = categoryService.addCategory(categoryCar(), userId).getId();

    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.ONE)
        .build())
    );
    transaction.setCategoryId(categoryId);
    transaction.setDescription("Transaction with price 1");
    transaction.setDate(LocalDate.now());

    transactionService.addTransaction(userId, transaction);
    Mockito.when(transactionService.getTransactionByIdAndUserId(42, userId)).thenReturn(Optional.of(transaction));
    // when

    try {
      transactionService.deleteTransaction(42, userId);
      fail();
    } catch (EmptyResultDataAccessException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountByIdAndUserId(savedAccount.getId(), userId);

    assertThat(returnedAccount.isPresent(), is(true));
    assertThat(returnedAccount.get().getBalance(), is(BigDecimal.valueOf(1100, 2)));
  }

  @Test
  public void shouldRollbackAccountStateUpdateWhenUpdateFailsBecauseOfInvalidData() {

    // given
    long userId = userService.registerUser(userMirek()).getId();

    Account savedAccount = accountService.addAccount(Account.builder()
        .balance(BigDecimal.TEN)
        .name("Ing")
        .userId(userId)
        .build()
    );

    long categoryId = categoryService.addCategory(categoryOil(), userId).getId();

    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.ONE)
        .build())
    );
    transaction.setCategoryId(categoryId);
    transaction.setDescription("Trans with price 1");
    transaction.setDate(LocalDate.now());
    transaction.setUserId(userId);

    long transactionId = transactionService.addTransaction(userId, transaction).getId();

    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.TEN)
        .build()));
    transaction.setDate(null);

    // when
    try {
      transactionService.updateTransaction(transactionId, userId, transaction);
      fail();
    } catch (UnsupportedOperationException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountByIdAndUserId(savedAccount.getId(), userId);

    assertThat(returnedAccount.isPresent(), is(true));
    assertThat(returnedAccount.get().getBalance(), is(BigDecimal.valueOf(1100, 2)));
  }

}
