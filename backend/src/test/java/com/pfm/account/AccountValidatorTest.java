package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_LUKASZ_BALANCE_1124;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pfm.helpers.TestAccountProvider;
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
public class AccountValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountValidator accountValidator;

  @Test
  public void validateAccountForUpdate() {
    //when
    when(accountService.getAccountById(ACCOUNT_RAFAL_BALANCE_0.getId()))
        .thenReturn(Optional.empty());

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Account with id: " + ACCOUNT_RAFAL_BALANCE_0.getId() + " does not exist in database");

    //then
    accountValidator
        .validateAccountForUpdate(ACCOUNT_RAFAL_BALANCE_0.getId(), ACCOUNT_RAFAL_BALANCE_0);
  }

  @Test
  public void shouldReturnValidationErrorIfAccountNameAndBalanceIsEmpty() {
    //given
    long id = 10L;
    when(accountService.getAccountById(id)).thenReturn(Optional.of(ACCOUNT_LUKASZ_BALANCE_1124));
    Account account = Account.builder().id(id).name("").balance(null).build();

    //when
    List<String> result = accountValidator.validateAccountForUpdate(id, account);

    //then
    assertThat(result.size(), is(2));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NAME))));
    assertThat(result.get(1), is(equalTo(getMessage(EMPTY_ACCOUNT_BALANCE))));
  }

  @Test
  public void shouldNotFindDuplicateWhenNoOtherAccountsExists() {
    //given
    Account account = TestAccountProvider.ACCOUNT_ANDRZEJ_BALANCE_1_000_000;
    when(accountService.isAccountNameAlreadyUsed(any())).thenReturn(true);

    //when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(account);

    //then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }
}