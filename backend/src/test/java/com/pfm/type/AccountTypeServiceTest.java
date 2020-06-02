package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountTypeProvider.accountInvestment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRepository;
import com.pfm.account.type.AccountTypeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTypeServiceTest {

  private static final long MOCK_USER_ID = 999;

  @Mock
  private AccountTypeRepository accountTypeRepository;

  @InjectMocks
  private AccountTypeService accountTypeService;

  @Test
  public void shouldReturnErrorWhenAccountTypeDoNotExists() {
    // given
    int accountTypeId = 14;
    int userId = 10;

    when(accountTypeRepository.findByIdAndUserId(accountTypeId, userId)).thenReturn(Optional.empty());

    // when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      accountTypeService.getAccountTypeFromDbByIdAndUserId(accountTypeId, userId);
    });

    // then
    assertThat(exception.getMessage(), is(equalTo(String.format(getMessage(ACCOUNT_TYPE_ID_DOES_NOT_EXIST), accountTypeId))));
  }

  @Test
  public void shouldGetAccountTypeById() {
    // given
    AccountType accountType = accountInvestment();
    accountType.setId(1L);

    when(accountTypeRepository.findByIdAndUserId(accountType.getId(), MOCK_USER_ID))
        .thenReturn(Optional.of(accountType));

    // when
    Optional<AccountType> returnedAccountType = accountTypeService
        .getAccountTypeByIdAndUserId(accountType.getId(), MOCK_USER_ID);

    // then
    assertTrue(returnedAccountType.isPresent());

    AccountType actualAccountType = returnedAccountType.get();

    assertThat(actualAccountType.getId(), is(equalTo(accountType.getId())));
    assertThat(actualAccountType.getName(), is(equalTo(accountType.getName())));
  }

}
