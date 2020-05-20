package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeService;
import com.pfm.auth.UserProvider;
import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.history.HistoryEntryService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

class AccountControllerTransactionalTest extends IntegrationTestsBase {

  @SpyBean
  private HistoryEntryService historyEntryService;

  @MockBean
  private UserProvider userProvider;

  @SpyBean
  private AccountTypeService accountTypeService;

  @SpyBean
  private CurrencyService currencyService;

  @SpyBean
  private AccountService accountService;

  @Autowired
  private AccountController accountController;

  @Override
  @BeforeEach
  public void before() {
    super.before();
    userId = userService.registerUser(userZdzislaw()).getId();
    when(userProvider.getCurrentUserId()).thenReturn(userId);
    currencyService.addDefaultCurrencies(userId);
    accountTypeService.addDefaultAccountTypes(userId);
  }

  @Test
  void shouldRollbackTransactionWhenAccountAddFailed() {
    // given
    Account account = accountMbankBalance10();
    doThrow(IllegalStateException.class).when(historyEntryService).addHistoryEntryOnAdd(any(Object.class), anyLong());
    when(currencyService.findCurrencyByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new Currency()));
    when(accountTypeService.getAccountTypeByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new AccountType()));

    // when
    try {
      accountController.addAccount(convertAccountToAccountRequest(account));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(accountService.getAccounts(userId), hasSize(0));
  }

  @Test
  void shouldRollbackTransactionWhenAccountUpdateFailed() {
    // given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));
    final Long accountId = accountService.saveAccount(userId, account).getId();

    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setName("updatedName");
    updatedAccount.setCurrency(currencyService.getCurrencies(userId).get(0));
    updatedAccount.setType(accountTypeService.getAccountTypes(userId).get(0));

    doThrow(IllegalStateException.class).when(accountService).updateAccount(any(Long.class), any(Long.class), any(Account.class));

    // when
    try {
      accountController.updateAccount(accountId, convertAccountToAccountRequest(updatedAccount));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));

  }

  @Test
  void shouldRollbackTransactionWhenAccountDeleteFailed() {
    // given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));
    final Long accountId = accountService.saveAccount(userId, account).getId();

    doThrow(IllegalStateException.class).when(accountService).deleteAccount(accountId);

    // when
    try {
      accountController.deleteAccount(accountId);
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    // then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));
  }
}
