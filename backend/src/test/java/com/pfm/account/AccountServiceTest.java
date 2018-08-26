package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ANDRZEJ_BALANCE_1_000_000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MARCIN_BALANCE_10_99;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MARIUSZ_BALANCE_200;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_SLAWEK_BALANCE_9;
import static com.pfm.helpers.TestAccountProvider.MOCK_ACCOUNT_ID;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  public void shouldGetAccount() {
    //given
    when(accountRepository.findById(ACCOUNT_RAFAL_BALANCE_0.getId()))
        .thenReturn(Optional.of(ACCOUNT_RAFAL_BALANCE_0));

    //when
    Optional<Account> returnedAccount = accountService
        .getAccountById(ACCOUNT_RAFAL_BALANCE_0.getId());

    //then
    assertNotNull(returnedAccount);

    Account account = returnedAccount.orElse(null);
    assertNotNull(account);
    assertThat(account.getId(), is(equalTo(ACCOUNT_RAFAL_BALANCE_0.getId())));
    assertThat(account.getName(), is(equalTo(ACCOUNT_RAFAL_BALANCE_0.getName())));
    assertThat(account.getBalance(), is(equalTo(ACCOUNT_RAFAL_BALANCE_0.getBalance())));
  }

  @Test
  public void shouldGetAllAccounts() {
    //given
    when(accountRepository.findAll()).thenReturn(
        Arrays.asList(ACCOUNT_SLAWEK_BALANCE_9, ACCOUNT_ANDRZEJ_BALANCE_1_000_000));

    //when
    List<Account> actualAccountsList = accountService.getAccounts();

    //then
    assertThat(actualAccountsList.size(), is(2));

    // accounts should be sorted by id
    assertThat(ACCOUNT_ANDRZEJ_BALANCE_1_000_000.getId(),
        lessThan(ACCOUNT_SLAWEK_BALANCE_9.getId()));

    Account account1 = actualAccountsList.get(0);
    assertThat(account1.getId(), is(equalTo(ACCOUNT_ANDRZEJ_BALANCE_1_000_000.getId())));
    assertThat(account1.getName(), is(equalTo(ACCOUNT_ANDRZEJ_BALANCE_1_000_000.getName())));
    assertThat(account1.getBalance(),
        is(equalTo(ACCOUNT_ANDRZEJ_BALANCE_1_000_000.getBalance())));

    Account account2 = actualAccountsList.get(1);
    assertThat(account2.getId(), is(equalTo(ACCOUNT_SLAWEK_BALANCE_9.getId())));
    assertThat(account2.getName(), is(equalTo(ACCOUNT_SLAWEK_BALANCE_9.getName())));
    assertThat(account2.getBalance(), is(equalTo(ACCOUNT_SLAWEK_BALANCE_9.getBalance())));
  }

  @Test
  public void shouldSaveAccount() {
    //given
    when(accountRepository.save(ACCOUNT_MARCIN_BALANCE_10_99))
        .thenReturn(ACCOUNT_MARCIN_BALANCE_10_99);

    //when
    Account account = accountService.addAccount(ACCOUNT_MARCIN_BALANCE_10_99);

    //then
    assertNotNull(account);
    assertThat(account.getId(), is(equalTo(ACCOUNT_MARCIN_BALANCE_10_99.getId())));
    assertThat(account.getName(), is(equalTo(ACCOUNT_MARCIN_BALANCE_10_99.getName())));
    assertThat(account.getBalance(), is(equalTo(ACCOUNT_MARCIN_BALANCE_10_99.getBalance())));
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

    Account updatedAccount = Account.builder()
        .balance(BigDecimal.TEN)
        .name("Zaskurniaki")
        .build();

    Account expectedAccount = Account.builder()
        .balance(updatedAccount.getBalance())
        .name(updatedAccount.getName())
        .id(ACCOUNT_MARIUSZ_BALANCE_200.getId())
        .build();

    when(accountRepository.findById(expectedAccount.getId()))
        .thenReturn(Optional.of(ACCOUNT_MARIUSZ_BALANCE_200));

    //when
    accountService.updateAccount(expectedAccount.getId(), updatedAccount);

    //then
    verify(accountRepository, times(1)).save(expectedAccount);
  }

  @Test
  public void shouldCheckIfAccountExists() {
    //given
    when(accountRepository.existsById(MOCK_ACCOUNT_ID)).thenReturn(true);

    //when
    accountService.idExist(MOCK_ACCOUNT_ID);

    //then
    verify(accountRepository).existsById(MOCK_ACCOUNT_ID);
  }

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldThrowExceptionCausedByIdNotExist() {

    expectedEx.expect(IllegalStateException.class);
    expectedEx.expectMessage("Account with id: " + MOCK_ACCOUNT_ID + " does not exist in database");
    //given
    when(accountRepository.findById(MOCK_ACCOUNT_ID)).thenReturn(Optional.empty());

    //when
    accountService.updateAccount(MOCK_ACCOUNT_ID, ACCOUNT_MARCIN_BALANCE_10_99);

  }
}
