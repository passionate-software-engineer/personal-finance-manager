package com.pfm.services;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountRepository;
import com.pfm.account.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  private static final String NAME = "Jeff Masters";
  private static final Long ID_1 = 1L;
  private static final BigDecimal BALANCE = BigDecimal.TEN;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountService accountService;

  @Before
  public void mockAccount() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(createMockAccount()));
    when(accountRepository.findAll()).thenReturn(Collections.singletonList(createMockAccount()));
    when(accountRepository.save(createMockAccount())).thenReturn(createMockAccount());
  }

  @Test
  public void shouldGetAccount() {
    //given

    //when
    Account actualAccount = createMockAccount();

    //then
    assertNotNull(actualAccount);
    assertThat(ID_1, is(equalTo(actualAccount.getId())));
    assertThat(NAME, is(equalTo(actualAccount.getName())));
    assertThat(BALANCE, is(equalTo(actualAccount.getBalance())));
  }

  @Test
  public void shouldGetAllAccounts() {
    //given

    //when
    List<Account> actualAccountsList = accountService.getAccounts();

    //then
    assertFalse(actualAccountsList.isEmpty()); // TODO unify tests - merge both approaches
    Account actualAccount = actualAccountsList.get(0);
    assertThat(ID_1, is(equalTo(actualAccount.getId())));
    assertThat(NAME, is(equalTo(actualAccount.getName())));
    assertThat(BALANCE, is(equalTo(actualAccount.getBalance())));
  }

  @Test
  public void shouldSaveAccount() {
    //given

    //when
    Account actualAccount = accountService.addAccount(createMockAccount());

    //then
    assertNotNull(actualAccount);
    assertThat(ID_1, is(equalTo(actualAccount.getId())));
    assertThat(NAME, is(equalTo(actualAccount.getName())));
    assertThat(BALANCE, is(equalTo(actualAccount.getBalance())));
  }

  @Test
  public void shouldDeleteAccount() {
    //given

    //when
    accountService.deleteAccount(1L);

    //then
    verify(accountRepository, times(1)).deleteById(1L);
  }

  @Test
  public void shouldUpdateAccount() {
    //given

    //when
    accountService.updateAccount(ID_1, createMockAccount());

    //then
    verify(accountRepository, times(1)).save(createMockAccount());
  }

  private Account createMockAccount() {
    return Account.builder()
        .id(ID_1)
        .name(NAME)
        .balance(BALANCE)
        .build();
  }
}
