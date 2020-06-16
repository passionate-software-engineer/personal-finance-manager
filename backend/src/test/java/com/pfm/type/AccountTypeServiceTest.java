package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountTypeProvider.accountCredit;
import static com.pfm.helpers.TestAccountTypeProvider.accountInvestment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRepository;
import com.pfm.account.type.AccountTypeService;
import java.util.Arrays;
import java.util.List;
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

  @Test
  public void shouldThrowExceptionCausedByIdNotExistInUpdateMethod() {
    // given
    long id = 1;
    when(accountTypeRepository.findByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.empty());

    // when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      accountTypeService.updateAccountType(id, MOCK_USER_ID, accountInvestment());
    });

    // then
    assertThat(exception.getMessage(), is("Account type with id: " + id + " does not exist in database"));
  }

  @Test
  public void shouldGetAllAccountTypes() {
    // given
    AccountType accountTypeCredit = accountCredit();
    accountTypeCredit.setId(1L);
    AccountType accountTypeInvestment = accountInvestment();
    accountTypeInvestment.setId(2L);

    when(accountTypeRepository.findByUserId(MOCK_USER_ID)).thenReturn(Arrays.asList(accountTypeCredit, accountTypeInvestment));

    // when
    List<AccountType> actualAccountTypesList = accountTypeService.getAccountTypes(MOCK_USER_ID);

    // then
    assertThat(actualAccountTypesList.size(), is(2));

    // account types should be sorted by id
    assertThat(accountTypeCredit.getId(), lessThan(accountTypeInvestment.getId()));

    AccountType accountType1 = actualAccountTypesList.get(0);
    assertThat(accountType1.getId(), is(equalTo(accountTypeCredit.getId())));
    assertThat(accountType1.getName(), is(equalTo(accountTypeCredit.getName())));

    AccountType accountType2 = actualAccountTypesList.get(1);
    assertThat(accountType2.getId(), is(equalTo(accountTypeInvestment.getId())));
    assertThat(accountType2.getName(), is(equalTo(accountTypeInvestment.getName())));
  }

  @Test
  public void shouldSaveAccountType() {
    // given
    AccountType accountTypeToSave = accountInvestment();
    accountTypeToSave.setId(1L);
    when(accountTypeRepository.save(accountTypeToSave)).thenReturn(accountTypeToSave);

    // when
    AccountType accountType = accountTypeService.saveAccountType(MOCK_USER_ID, accountTypeToSave);

    // then
    assertNotNull(accountType);
    assertThat(accountType.getId(), is(equalTo(accountTypeToSave.getId())));
    assertThat(accountType.getName(), is(equalTo(accountTypeToSave.getName())));
  }

  @Test
  public void shouldDeleteAccountType() {
    // given

    // when
    accountTypeService.deleteAccountType(1L);

    // then
    verify(accountTypeRepository, times(1)).deleteById(1L);
  }

  @Test
  public void shouldUpdateAccountType() {
    // given
    AccountType accountType = accountInvestment();
    when(accountTypeRepository.findByIdAndUserId(accountType.getId(), MOCK_USER_ID)).thenReturn(Optional.of(accountType));
    when(accountTypeRepository.save(accountType)).thenReturn(accountType);

    // when
    accountTypeService.updateAccountType(accountType.getId(), MOCK_USER_ID, accountType);

    // then
    verify(accountTypeRepository).findByIdAndUserId(accountType.getId(), MOCK_USER_ID);
    verify(accountTypeRepository).save(accountType);
  }

}
