package com.pfm.account;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
    Account accountToVerify = Account.builder().id(1L).name("Food").balance(BigDecimal.TEN).build();
    when(accountService.getAccountById(1)).thenReturn(Optional.empty());

    //then
    accountValidator.validateAccountForUpdate(1, accountToVerify);
  }
}