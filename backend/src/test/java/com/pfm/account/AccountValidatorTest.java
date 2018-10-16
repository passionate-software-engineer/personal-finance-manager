package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountValidatorTest {

  private long mockUserId = 999;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountValidator accountValidator;

  @Test
  public void validateAccountForUpdate() {

    //given
    long id = 1;

    //when
    when(accountService.getAccountByIdAndUserId(id, mockUserId))
        .thenReturn(Optional.empty());

    Throwable exception = assertThrows(IllegalStateException.class, () -> accountValidator.validateAccountForUpdate(id, mockUserId, new Account()));

    //then
    assertThat(exception.getMessage(), is(equalTo("Account with id: " + id + " does not exist in database")));
  }

  @Test
  public void shouldReturnValidationErrorIfAccountNameAndBalanceIsEmpty() {

    //given
    long id = 10L;
    when(accountService.getAccountByIdAndUserId(id, mockUserId)).thenReturn(Optional.of(accountMbankBalance10()));
    Account account = Account.builder().id(id).name("").balance(null).build();

    //when
    List<String> result = accountValidator.validateAccountForUpdate(id, mockUserId, account);

    //then
    assertThat(result.size(), is(2));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NAME))));
    assertThat(result.get(1), is(equalTo(getMessage(EMPTY_ACCOUNT_BALANCE))));
  }

  @Test
  public void shouldNotFindDuplicateWhenNoOtherAccountsExists() {
    //given
    when(accountService.isAccountNameAlreadyUsed(any())).thenReturn(true);

    //when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(accountMbankBalance10());

    //then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }
}