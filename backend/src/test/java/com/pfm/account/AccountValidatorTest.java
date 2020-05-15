package com.pfm.account;

import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.INVALID_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.INVALID_CONTROL_SUM_FOR_POLISH_ACCOUNT_NUMBER;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class AccountValidatorTest {

  private static final long MOCK_USER_ID = 999;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountValidator accountValidator;

  @Test
  public void validateAccountForUpdate() {
    // given
    long id = 1;

    // when
    when(accountService.getAccountByIdAndUserId(id, MOCK_USER_ID))
        .thenReturn(Optional.empty());

    Throwable exception = assertThrows(IllegalStateException.class, () -> accountValidator.validateAccountForUpdate(id, MOCK_USER_ID, new Account()));

    // then
    assertThat(exception.getMessage(), is(equalTo("Account with id: " + id + " does not exist in database")));
  }

  @Test
  public void shouldReturnValidationErrorIfAccountNameAndBalanceIsEmpty() {
    // given
    long id = 10L;
    String bankAccountNumber = "11195000012006857419590002";
    when(accountService.getAccountByIdAndUserId(id, MOCK_USER_ID)).thenReturn(Optional.of(accountMbankBalance10()));
    Account account = Account.builder().id(id).bankAccountNumber(bankAccountNumber).name("").balance(null).build();

    // when
    List<String> result = accountValidator.validateAccountForUpdate(id, MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(2));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NAME))));
    assertThat(result.get(1), is(equalTo(getMessage(EMPTY_ACCOUNT_BALANCE))));
  }

  @Test
  public void shouldNotFindDuplicatewhenNoOtherAccountsExists() {
    // given
    String bankAccountNumber = null;
    long accountId = 9984L;
    Account account = Account.builder()
        .id(accountId)
        .name("Db")
        .bankAccountNumber(bankAccountNumber)
        .balance(BigDecimal.TEN)
        .build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NUMBER))));
  }

  @Test
  public void shouldReturnValidationErrorIfBankAccountNumberIsNull() {
    // given
    String bankAccountNumber = null;
    long accountId = 9984L;
    Account account = Account.builder()
        .id(accountId)
        .name("Db")
        .bankAccountNumber(bankAccountNumber)
        .balance(BigDecimal.TEN)
        .build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NUMBER))));
  }

  @Test
  public void shouldReturnValidationErrorIfBankAccountNumberIsEmpty() {
    // given
    String bankAccountNumber = "";
    long accountId = 9984L;
    Account account = Account.builder()
        .id(accountId)
        .name("Db")
        .bankAccountNumber(bankAccountNumber)
        .balance(BigDecimal.TEN)
        .build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(EMPTY_ACCOUNT_NUMBER))));
  }

  @ParameterizedTest
  @MethodSource("shouldReturnValidationErrorForInvalidAccountNumberParams")
  public void shouldReturnValidationErrorForInvalidAccountNumber(String invalidBankAccountNumber) {
    // given
    long accountId = 9984L;
    Account account = Account.builder()
        .id(accountId)
        .name("Db")
        .bankAccountNumber(invalidBankAccountNumber)
        .balance(BigDecimal.TEN)
        .build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(INVALID_ACCOUNT_NUMBER))));
  }

  private static Stream<Object> shouldReturnValidationErrorForInvalidAccountNumberParams() {
    final String tooLong = "11111222223333344444555556666677777888889999900000";
    final String tooShort = "12234567890000";
    final String lengthCorrectButContainsLetter = "q4762212283700000001782390";
    final String lengthCorrectButContainsSpace = "74762 12283700000001782390";
    final String lengthCorrectButContainsDot = "74762.12283700000001782390";
    final String lengthCorrectButContainsOnlyLetters = "qtghbnjuyesdcxsmkloiuyhgtr";

    return Stream.of(
        Arguments.of(tooLong),
        Arguments.of(tooShort),
        Arguments.of(lengthCorrectButContainsOnlyLetters),
        Arguments.of(lengthCorrectButContainsDot),
        Arguments.of(lengthCorrectButContainsSpace),
        Arguments.of(lengthCorrectButContainsLetter));
  }

  @ParameterizedTest
  @MethodSource("shouldReturnTrueForCorrectAccountNumbersParams")
  void shouldReturnTrueForCorrectAccountNumbers(String bankAccountNumber) {
    // given
    long id = 7639873L;
    Account account = Account.builder().id(id).bankAccountNumber(bankAccountNumber).name("saving").balance(BigDecimal.TEN).build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(0));
  }

  private static Stream<Object> shouldReturnTrueForCorrectAccountNumbersParams() {

    return Stream.of(
        Arguments.of("11195000012006857419590002"),
        Arguments.of("58105014451000009715050879"),
        Arguments.of("63105000441000002456124318"),
        Arguments.of("19203000451130000012272270"));
  }

  @ParameterizedTest
  @MethodSource("shouldReturnFalseForMistakenBankAccountNumbersHavingIncorrectControlSumForPolishAccountParams")
  void shouldReturnFalseForMistakenBankAccountNumbersHavingIncorrectControlSumForPolishAccount(String bankAccountNumber) {
    // given
    long id = 7639873L;
    Account account = Account.builder().id(id).bankAccountNumber(bankAccountNumber).name("saving").balance(BigDecimal.TEN).build();

    // when
    List<String> result = accountValidator.validateAccountIncludingNameDuplication(MOCK_USER_ID, account);

    // then
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(getMessage(INVALID_CONTROL_SUM_FOR_POLISH_ACCOUNT_NUMBER))));
  }

  private static Stream<Object> shouldReturnFalseForMistakenBankAccountNumbersHavingIncorrectControlSumForPolishAccountParams() {

    return Stream.of(
        Arguments.of("11195000012606857419590002"),
        Arguments.of("58105014451700009715050879"),
        Arguments.of("63105000441700002456124318"),
        Arguments.of("19203000451830000012272270"));
  }
}
