package com.pfm.transaction.import1;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.transaction.import1.csv.CsvImportController.TEXT_CSV;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestAccountProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

@SuppressFBWarnings("DM_DEFAULT_ENCODING")
@SuppressWarnings("PMD.UnusedPrivateMethod")
class CsvImportControllerTest extends IntegrationTestsBase {

  private static final long MOCK_ACCOUNT_ID = 99L;
  private static final long MOCK_USER_ID = 34L;

  @MockBean
  private CsvImportService csvImportService;

  @MockBean
  private AccountService accountService;

  @MockBean
  private UserProvider userProvider;

  @MockBean
  private FileHelper fileHelper;

  @BeforeEach
  public void setUp() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  void shouldReturnBadRequestForNonExistingUserAccounts() throws Exception {
    // given
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());
    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(Collections.emptyList());

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_BAD_REQUEST)));
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
  }

  @Test
  void shouldReturnBadRequestForNonExistingTargetAccount() throws Exception {
    // given
    Account account = TestAccountProvider.accountMbankBalance10();
    List<Account> userAccounts = List.of(account);
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());

    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(userAccounts);
    when(accountService.isAccountIdPresentInAccounts(anyLong(), eq(userAccounts))).thenReturn(false);

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_BAD_REQUEST)));

    verify(userProvider, times(1)).getCurrentUserId();
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
    verify(accountService, times(1)).isAccountIdPresentInAccounts(anyLong(), eq(userAccounts));
  }

  @Test
  void shouldReturnBadRequestForAnyUserAccountsWithoutAccountBankNumber() throws Exception {
    // given
    Account account = TestAccountProvider.accountMbankBalance10();
    Account accountWithoutBankAccountNumber = TestAccountProvider.accountIdeaBalance100000();
    accountWithoutBankAccountNumber.setBankAccountNumber("");
    List<Account> userAccounts = List.of(account, accountWithoutBankAccountNumber);
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());

    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(userAccounts);
    when(accountService.isAccountIdPresentInAccounts(anyLong(), eq(userAccounts))).thenReturn(true);
    when(accountService.getAccountsWithoutBankAccountNumber(userAccounts)).thenReturn(Collections.singletonList(accountWithoutBankAccountNumber));

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_BAD_REQUEST)));

    verify(userProvider, times(1)).getCurrentUserId();
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
    verify(accountService, times(1)).isAccountIdPresentInAccounts(anyLong(), eq(userAccounts));
    verify(accountService, times(1)).getAccountsWithoutBankAccountNumber(userAccounts);

  }

  @Test
  void shouldReturnBadRequestForTransactionParsingException() throws Exception {
    // given
    Account account = TestAccountProvider.accountMbankBalance10();
    List<Account> userAccounts = List.of(account);
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());

    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(List.of(account));
    when(accountService.isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts)).thenReturn(true);
    when(accountService.getAccountsWithoutBankAccountNumber(userAccounts)).thenReturn(Collections.emptyList());

    when(csvImportService.importTransactions(any(File.class), anyLong(), anyLong(), eq(userAccounts)))
        .thenThrow(TransactionsParsingException.class);

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR)));

    verify(userProvider, times(1)).getCurrentUserId();
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
    verify(accountService, times(1)).isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts);
    verify(accountService, times(1)).getAccountsWithoutBankAccountNumber(userAccounts);
    verify(fileHelper, times(1)).convertMultiPartFileToFile(file);

    verify(accountService, never()).getAccountByIdAndUserId(eq(MOCK_ACCOUNT_ID), anyLong());
  }

  @Test
  void shouldReturnBadRequestForNullFileContentType() throws Exception {
    // given
    MockMultipartFile file = new MockMultipartFile("file", "file", null, "file".getBytes());

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_BAD_REQUEST)));
  }

  @Test
  void shouldReturnBadRequestForNotAcceptedFileContentType() throws Exception {
    // given
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV + "e", "file".getBytes());

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_BAD_REQUEST)));
  }

  @Test
  void shouldReturnInternalServerErrorForIoExceptionThrownByFileHelperConvertingMultipartFileToFile() throws Exception {
    // given
    MockMultipartFile file = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());
    Account account = TestAccountProvider.accountIdeaBalance100000();
    List<Account> userAccounts = List.of(account);

    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(List.of(account));
    when(accountService.isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts)).thenReturn(true);
    when(accountService.getAccountsWithoutBankAccountNumber(userAccounts)).thenReturn(Collections.emptyList());
    when(fileHelper.convertMultiPartFileToFile(file)).thenThrow(IOException.class);

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, file);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR)));

    verify(userProvider, times(1)).getCurrentUserId();
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
    verify(accountService, times(1)).isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts);
    verify(accountService, times(1)).getAccountsWithoutBankAccountNumber(userAccounts);
    verify(fileHelper, times(1)).convertMultiPartFileToFile(file);

  }

  @Test
  void shouldReturnInternalServerErrorForIoExceptionThrownByCsvParser() throws Exception {
    // given
    Account account = TestAccountProvider.accountMbankBalance10();
    List<Account> userAccounts = List.of(account);
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, "file".getBytes());
    File file = new File("pathName");

    when(userProvider.getCurrentUserId()).thenReturn(MOCK_USER_ID);
    when(accountService.getAccounts(MOCK_USER_ID)).thenReturn(List.of(account));
    when(accountService.isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts)).thenReturn(true);
    when(accountService.getAccountsWithoutBankAccountNumber(userAccounts)).thenReturn(Collections.emptyList());

    when(fileHelper.convertMultiPartFileToFile(mockMultipartFile)).thenReturn(file);

    when(csvImportService.importTransactions(eq(file), eq(MOCK_USER_ID), eq(MOCK_ACCOUNT_ID), eq(userAccounts)))
        .thenThrow(TransactionsParsingException.class);

    // when
    final int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(MOCK_ACCOUNT_ID, mockMultipartFile);

    // then
    assertThat(status, is(equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR)));

    verify(userProvider, times(1)).getCurrentUserId();
    verify(accountService, times(1)).getAccounts(MOCK_USER_ID);
    verify(accountService, times(1)).isAccountIdPresentInAccounts(MOCK_ACCOUNT_ID, userAccounts);
    verify(fileHelper, times(1)).convertMultiPartFileToFile(mockMultipartFile);
    verify(csvImportService, times(1)).importTransactions(eq(file), eq(MOCK_USER_ID), eq(MOCK_ACCOUNT_ID), eq(userAccounts));
  }
}
