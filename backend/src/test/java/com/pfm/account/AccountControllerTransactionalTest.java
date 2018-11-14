package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestUsersProvider.userZdzislaw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.pfm.auth.UserProvider;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.history.HistoryEntryService;
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
  private AccountService accountService;

  @Autowired
  private AccountController accountController;

  @BeforeEach
  public void before() {
    super.before();
    userId = userService.registerUser(userZdzislaw()).getId();
    when(userProvider.getCurrentUserId()).thenReturn(userId);
  }

  @Test
  void shouldRollbackAddedAccount() {

    //given
    Account account = accountMbankBalance10();
    doThrow(IllegalStateException.class).when(historyEntryService).addHistoryEntryOnAdd(any(Object.class), any(Long.class));

    // when
    try {
      accountController.addAccount(convertAccountToAccountRequest(account));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(accountService.getAccounts(userId), hasSize(0));
  }

  @Test
  void updateAccount() {

    //given
    Account account = accountMbankBalance10();
    final Long accountId = accountService.addAccount(userId, account).getId();

    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setName("updatedName");

    doThrow(IllegalStateException.class).when(accountService).updateAccount(any(Long.class), any(Long.class), any(Account.class));

    // when
    try {
      accountController.updateAccount(accountId, convertAccountToAccountRequest(updatedAccount));
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));

  }

  @Test
  void deleteAccount() {

    //given
    Account account = accountMbankBalance10();

    final Long accountId = accountService.addAccount(userId, account).getId();

    doThrow(IllegalStateException.class).when(accountService).deleteAccount(accountId);

    // when
    try {
      accountController.deleteAccount(accountId);
      fail();
    } catch (IllegalStateException ex) {
      assertNotNull(ex);
    }

    //then
    assertThat(historyEntryService.getHistoryEntries(userId), hasSize(0));
  }
}