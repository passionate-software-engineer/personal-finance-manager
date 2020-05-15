package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.helpers.TestAccountProvider;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  private static final long MOCK_USER_ID = 999;
  private static final String BANK_ACCOUNT_NUMBER = "11195000012006857419590002";
  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  public void shouldGetAccountById() {
    // given
    Account account = accountMbankBalance10();
    account.setId(1L);

    when(accountRepository.findByIdAndUserId(account.getId(), MOCK_USER_ID))
        .thenReturn(Optional.of(account));

    // when
    Optional<Account> returnedAccount = accountService
        .getAccountByIdAndUserId(account.getId(), MOCK_USER_ID);

    // then
    assertTrue(returnedAccount.isPresent());

    Account actualAccount = returnedAccount.get();

    assertThat(actualAccount.getId(), is(equalTo(account.getId())));
    assertThat(actualAccount.getName(), is(equalTo(account.getName())));
    assertThat(actualAccount.getBalance(), is(equalTo(account.getBalance())));
  }

  @Test
  public void shouldGetAllAccounts() {
    // given
    Account accountJacek = accountJacekBalance1000();
    accountJacek.setId(1L);
    Account accountMbank = accountMbankBalance10();
    accountMbank.setId(2L);

    when(accountRepository.findByUserId(MOCK_USER_ID)).thenReturn(List.of(accountMbank, accountJacek));

    // when
    List<Account> actualAccountsList = accountService.getAccounts(MOCK_USER_ID);

    // then
    assertThat(actualAccountsList.size(), is(2));

    // accounts should be sorted by id
    assertThat(accountJacek.getId(), lessThan(accountMbank.getId()));

    Account account1 = actualAccountsList.get(0);
    assertThat(account1.getId(), is(equalTo(accountJacek.getId())));
    assertThat(account1.getName(), is(equalTo(accountJacek.getName())));
    assertThat(account1.getBalance(), is(equalTo(accountJacek.getBalance())));

    Account account2 = actualAccountsList.get(1);
    assertThat(account2.getId(), is(equalTo(accountMbank.getId())));
    assertThat(account2.getName(), is(equalTo(accountMbank.getName())));
    assertThat(account2.getBalance(), is(equalTo(accountMbank.getBalance())));
  }

  @Test
  public void shouldSaveAccount() {
    // given
    Account accountToSave = accountMbankBalance10();
    accountToSave.setId(1L);
    when(accountRepository.save(accountToSave)).thenReturn(accountToSave);

    // when
    Account account = accountService.saveAccount(MOCK_USER_ID, accountToSave);

    // then
    assertNotNull(account);
    assertThat(account.getId(), is(equalTo(accountToSave.getId())));
    assertThat(account.getName(), is(equalTo(accountToSave.getName())));
    assertThat(account.getBalance(), is(equalTo(accountToSave.getBalance())));
  }

  @Test
  public void shouldDeleteAccount() {
    // given

    // when
    accountService.deleteAccount(1L);

    // then
    verify(accountRepository, times(1)).deleteById(1L);
  }

  @Test
  public void shouldUpdateAccount() {
    // given

    Account updatedAccount = Account.builder()
        .balance(BigDecimal.TEN)
        .bankAccountNumber(BANK_ACCOUNT_NUMBER)
        .name("Zaskurniaki")
        .build();

    when(accountRepository.findByIdAndUserId(1L, MOCK_USER_ID)).thenReturn(Optional.of(accountMbankBalance10()));

    // when
    accountService.updateAccount(1L, MOCK_USER_ID, updatedAccount);

    // then
    verify(accountRepository, times(1)).save(updatedAccount);
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExistInUpdateMethod() {
    // given
    long id = 1;
    when(accountRepository.findByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    // when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      accountService.updateAccount(id, MOCK_USER_ID, accountMbankBalance10());
    });

    // then
    assertThat(exception.getMessage(), is("Account with id: " + id + " does not exist in database"));
  }

  @Test
  public void shouldThrowExceptionCausedByIdNotExistInGetMethod() {
    // given
    long id = 1;
    when(accountRepository.findByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    // when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      accountService.getAccountFromDbByIdAndUserId(id, MOCK_USER_ID);
    });

    // then
    assertThat(exception.getMessage(), is("Account with id: " + id + " does not exist in database"));
  }

  @Test
  public void shouldReturnAccountsWithoutBankAccountNumber() {
    // given
    Account account1 = TestAccountProvider.accountJacekBalance1000();
    Account account2 = TestAccountProvider.accountMbankBalance10();
    Account accountWithoutBankAccountNumber = TestAccountProvider.accountIngBalance9999();
    accountWithoutBankAccountNumber.setBankAccountNumber("");

    List<Account> allAccountList = List.of(account1, account2, accountWithoutBankAccountNumber);
    List<Account> expected = List.of(accountWithoutBankAccountNumber);

    // when
    final Collection<Account> actual = accountService.getAccountsWithoutBankAccountNumber(allAccountList);

    // then
    assertThat(actual, is(equalTo(expected)));
    verify(accountRepository, never()).findByUserId(MOCK_USER_ID);
  }

  @Test
  public void shouldReturnEmptyListForAllAccountsHavingBankAccountNumber() {
    // given
    Account account1 = TestAccountProvider.accountJacekBalance1000();
    Account account2 = TestAccountProvider.accountMbankBalance10();
    Account account3 = TestAccountProvider.accountIngBalance9999();

    List<Account> allAccountList = List.of(account1, account2, account3);
    List<Account> expected = Collections.emptyList();

    // when
    final Collection<Account> actual = accountService.getAccountsWithoutBankAccountNumber(allAccountList);

    // then
    assertThat(actual, is(equalTo(expected)));
    verify(accountRepository, never()).findByUserId(MOCK_USER_ID);
  }

}
