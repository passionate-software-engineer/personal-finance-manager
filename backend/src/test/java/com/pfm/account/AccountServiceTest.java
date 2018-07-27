package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ADAM_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JUREK_BALANCE_10_99;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MATEUSZ_BALANCE_200;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_PIOTR_BALANCE_9;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_SEBASTIAN_BALANCE_1_000_000;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  public void shouldGetAccount() {
    //given
    when(accountRepository.findById(ACCOUNT_ADAM_BALANCE_0.getId()))
        .thenReturn(Optional.of(ACCOUNT_ADAM_BALANCE_0));

    //when
    Optional<Account> returnedAccount = accountService
        .getAccountById(ACCOUNT_ADAM_BALANCE_0.getId());

    //then
    assertNotNull(returnedAccount);
    assertThat(returnedAccount.isPresent(), is(true));

    Account account = returnedAccount.get();
    assertThat(account.getId(), is(equalTo(ACCOUNT_ADAM_BALANCE_0.getId())));
    assertThat(account.getName(), is(equalTo(ACCOUNT_ADAM_BALANCE_0.getName())));
    assertThat(account.getBalance(), is(equalTo(ACCOUNT_ADAM_BALANCE_0.getBalance())));
  }

  @Test
  public void shouldGetAllAccounts() {
    //given
    when(accountRepository.findAll()).thenReturn(
        Arrays.asList(ACCOUNT_PIOTR_BALANCE_9, ACCOUNT_SEBASTIAN_BALANCE_1_000_000));

    //when
    List<Account> actualAccountsList = accountService.getAccounts();

    //then
    assertThat(actualAccountsList.size(), is(2));

    // accounts should be sorted by id
    assertThat(ACCOUNT_SEBASTIAN_BALANCE_1_000_000.getId(),
        lessThan(ACCOUNT_PIOTR_BALANCE_9.getId()));

    Account account1 = actualAccountsList.get(0);
    assertThat(account1.getId(), is(equalTo(ACCOUNT_SEBASTIAN_BALANCE_1_000_000.getId())));
    assertThat(account1.getName(), is(equalTo(ACCOUNT_SEBASTIAN_BALANCE_1_000_000.getName())));
    assertThat(account1.getBalance(),
        is(equalTo(ACCOUNT_SEBASTIAN_BALANCE_1_000_000.getBalance())));

    Account account2 = actualAccountsList.get(1);
    assertThat(account2.getId(), is(equalTo(ACCOUNT_PIOTR_BALANCE_9.getId())));
    assertThat(account2.getName(), is(equalTo(ACCOUNT_PIOTR_BALANCE_9.getName())));
    assertThat(account2.getBalance(), is(equalTo(ACCOUNT_PIOTR_BALANCE_9.getBalance())));
  }

  @Test
  public void shouldSaveAccount() {
    //given
    when(accountRepository.save(ACCOUNT_JUREK_BALANCE_10_99))
        .thenReturn(ACCOUNT_JUREK_BALANCE_10_99);

    //when
    Account account = accountService.addAccount(ACCOUNT_JUREK_BALANCE_10_99);

    //then
    assertNotNull(account);
    assertThat(account.getId(), is(equalTo(ACCOUNT_JUREK_BALANCE_10_99.getId())));
    assertThat(account.getName(), is(equalTo(ACCOUNT_JUREK_BALANCE_10_99.getName())));
    assertThat(account.getBalance(), is(equalTo(ACCOUNT_JUREK_BALANCE_10_99.getBalance())));
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
        .id(ACCOUNT_MATEUSZ_BALANCE_200.getId())
        .build();

    when(accountRepository.findById(expectedAccount.getId()))
        .thenReturn(Optional.of(ACCOUNT_MATEUSZ_BALANCE_200));

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

  // TODO assert exact exception message - user Rule
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionCausedByIdNotExist() {
    //given
    when(accountRepository.findById(MOCK_ACCOUNT_ID)).thenReturn(Optional.empty());

    //when
    accountService.updateAccount(MOCK_ACCOUNT_ID, ACCOUNT_JUREK_BALANCE_10_99);
  }
}
