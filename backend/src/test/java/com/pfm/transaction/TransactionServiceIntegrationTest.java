package com.pfm.transaction;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.flywaydb.core.Flyway;
import org.junit.Before;
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

  @Autowired
  protected Flyway flyway;

  @Autowired
  UserService userService;

  @SpyBean
  private TransactionService transactionService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private CategoryService categoryService;

  private long userId;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
    userId = userService.registerUser(userZdzislaw()).getId();
  }

  @Test
  public void shouldRollbackAccountStateUpdateWhenSaveFailsBecauseOfInvalidData() {

    // given
    Account account = accountMbankBalance10();
    account.setUserId(userId);
    Account savedAccount = accountService.addAccount(userId, account);

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
    assertThat(returnedAccount.get().getBalance(), is(BigDecimal.valueOf(1000, 2)));
  }

  @Test
  public void shouldRollbackAccountStateUpdateWhenDeleteFailsBecauseOfInvalidData() {

    // given
    Account account = accountMbankBalance10();
    account.setUserId(userId);
    Account savedAccount = accountService.addAccount(userId, account);

    Category category = categoryCar();
    category.setUserId(userId);

    long categoryId = categoryService.addCategory(category, userId).getId();

    Transaction transaction = new Transaction();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(BigDecimal.ONE)
        .build())
    );
    transaction.setCategoryId(categoryId);
    transaction.setDescription("Transaction with price 1");
    transaction.setDate(LocalDate.now());
    transaction.setUserId(userId);

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
    Account account = accountMbankBalance10();
    account.setUserId(userId);
    Account savedAccount = accountService.addAccount(userId, account);

    Category category = categoryOil();
    category.setUserId(userId);

    long categoryId = categoryService.addCategory(category, userId).getId();

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
    } catch (DataIntegrityViolationException ex) {
      assertNotNull(ex); // just not to leave empty
    }

    // then
    Optional<Account> returnedAccount = accountService.getAccountByIdAndUserId(savedAccount.getId(), userId);

    assertThat(returnedAccount.isPresent(), is(true));
    assertThat(returnedAccount.get().getBalance(), is(BigDecimal.valueOf(1100, 2)));
  }

  @Test
  public void shouldCheckIfTransactionExistByAccountId() {

    //given
    Account account = accountMbankBalance10();
    account.setUserId(userId);
    Account savedAccount = accountService.addAccount(userId, account);

    long categoryId = categoryService.addCategory(categoryHome(), userId).getId();

    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setUserId(userId);
    AccountPriceEntry accountPriceEntry = AccountPriceEntry.builder()
        .accountId(savedAccount.getId())
        .price(convertDoubleToBigDecimal(10))
        .build();
    transaction.setAccountPriceEntries(Collections.singletonList(accountPriceEntry));
    transaction.setCategoryId(categoryId);

    Long transactionId = transactionService.addTransaction(userId, transaction).getId();

    //when
    assertTrue(transactionService.transactionExistByAccountId(savedAccount.getId()));

    transactionService.deleteTransaction(transactionId, userId);

    assertFalse(transactionService.transactionExistByAccountId(savedAccount.getId()));

  }

  @Test
  public void shouldCheckIfTransactionExistByCategoryId() {

    //given
    Account account = accountMbankBalance10();
    account.setUserId(userId);
    Account savedAccount = accountService.addAccount(userId, account);

    long categoryId = categoryService.addCategory(categoryHome(), userId).getId();

    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setUserId(userId);
    AccountPriceEntry accountPriceEntry = AccountPriceEntry.builder()
        .accountId(savedAccount.getId()
        ).price(convertDoubleToBigDecimal(10))
        .build();
    transaction.setAccountPriceEntries(Collections.singletonList(accountPriceEntry));
    transaction.setCategoryId(categoryId);

    Long transactionId = transactionService.addTransaction(userId, transaction).getId();

    //when
    assertTrue(transactionService.transactionExistByCategoryId(categoryId));

    transactionService.deleteTransaction(transactionId, userId);

    assertFalse(transactionService.transactionExistByCategoryId(categoryId));

  }

}
