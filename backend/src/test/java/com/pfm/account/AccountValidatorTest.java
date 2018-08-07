package com.pfm.account;

import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
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
    thrown.expectMessage(
        "Account with id: " + ACCOUNT_RAFAL_BALANCE_0.getId() + " does not exist in database");

    //then
    accountValidator
        .validateAccountForUpdate(ACCOUNT_RAFAL_BALANCE_0.getId(), ACCOUNT_RAFAL_BALANCE_0);
  }

  @Test
  public void shouldValidateAccountIfAccountNameIsEmpty() {
    //given
    Account account = Account.builder().id(10L).balance(BigDecimal.valueOf(100)).build();
    //when
    accountValidator.validate(account);
    //then
    assertThat(accountValidator.validate(account).get(0), is(equalTo("Brak nazwy konta")));
  }

  @Test
  public void shouldCheckForDuplicatedName() {
    //given
    Account account = Account.builder().id(1L).balance(BigDecimal.valueOf(100)).build();
    List<String> validationResults = new ArrayList<>();
    //when
    accountValidator.checkForDuplicatedName(validationResults, account);
    //then
    Assert.assertThat(validationResults.size(), is(0));
  }
}