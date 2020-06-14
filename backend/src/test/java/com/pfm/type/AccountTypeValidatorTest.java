package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_TYPE_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountTypeProvider.accountInvestment;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeService;
import com.pfm.account.type.AccountTypeValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountTypeValidatorTest {

  private static final long MOCK_USER_ID = 999;

  @Mock
  private AccountTypeService accountTypeService;

  @InjectMocks
  private AccountTypeValidator accountTypeValidator;

  @Test
  public void validateAccountTypeForUpdate() {
    // given
    long id = 1;

    // when
    when(accountTypeService.getAccountTypeByIdAndUserId(id, MOCK_USER_ID))
        .thenReturn(Optional.empty());

    Throwable exception = assertThrows(IllegalStateException.class, () ->
        accountTypeValidator.validateAccountTypeForUpdate(id, MOCK_USER_ID, new AccountType()));

    // then
    assertThat(exception.getMessage(), is(equalTo("Account Type with id: " + id + " does not exist in database")));
  }

  @Test
  public void shouldReturnValidationErrorIfAccountTypeNameIsEmpty() {
    // given
    long id = 10L;
    when(accountTypeService.getAccountTypeByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.of(accountInvestment()));
    AccountType accountType = AccountType.builder().id(id).name("").build();

    // when
    List<String> result = accountTypeValidator.validateAccountTypeForUpdate(id, MOCK_USER_ID, accountType);

    // then
    assertThat(result.size(), is(2));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_TYPE_NAME))));
  }

  @Test
  public void shouldNotFindDuplicateWhenNoOtherAccountTypeExists() {
    // given
    when(accountTypeService.isAccountTypeNameAlreadyUsed(MOCK_USER_ID, accountInvestment().getName())).thenReturn(true);

    // when
    List<String> result = accountTypeValidator.validateAccountTypeIncludingNameDuplication(MOCK_USER_ID, accountInvestment());

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }
}
