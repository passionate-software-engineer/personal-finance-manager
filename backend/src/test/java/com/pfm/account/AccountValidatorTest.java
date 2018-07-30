package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ADAM_BALANCE_0;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountValidatorTest {

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountValidator accountValidator;

  @Test(expected = IllegalStateException.class)
  public void validateAccountForUpdate() {
    //when
    when(accountService.getAccountById(ACCOUNT_ADAM_BALANCE_0.getId()))
        .thenReturn(Optional.empty());

    //then
    accountValidator
        .validateAccountForUpdate(ACCOUNT_ADAM_BALANCE_0.getId(), ACCOUNT_ADAM_BALANCE_0);
  }
}