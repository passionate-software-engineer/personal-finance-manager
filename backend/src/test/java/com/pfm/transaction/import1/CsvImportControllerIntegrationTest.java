package com.pfm.transaction.import1;

import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.transaction.import1.csv.CsvImportController.TEXT_CSV;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.account.Account;
import com.pfm.account.type.AccountType;
import com.pfm.category.Category;
import com.pfm.currency.Currency;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

class CsvImportControllerIntegrationTest extends IntegrationTestsBase {

  private static final String ING_ACCOUNT_NUMBER = "06105014451000009715050457";
  private static final String ING_SUB_ACCOUNT_NUMBER1 = "58105014451000009715050879";

  @BeforeEach
  public void beforeEach() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  void shouldReturnEmptyListWhenAllTransactionsInFileDoNotHaveImportId() throws Exception {
    // Transactions in the file having importId (Nr transakcji) are not eligible for parsing as they do not mean real transactions, they mean only
    // that some amount was locked/secured on the account - the actual transaction comes after and has importId.

    // given
    final String path = "src/test/resources/csv/ing/not_valid_entries.csv";
    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(TARGET_BANK_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    // when
    List<Transaction> addedTransactions = callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile,
        importTargetAccountId);

    // then
    assertThat(addedTransactions.size(), is(equalTo(0)));
  }

  @Test
  void shouldReturnListOfTransactionsWhenUploadingCsvFile() throws Exception {
    // given
    final String path = "src/test/resources/csv/ing/original.csv";

    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(TARGET_BANK_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    // when
    List<Transaction> filteredTransactions = callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile,
        importTargetAccountId);

    // then
    assertThat(filteredTransactions.size(), is(equalTo(251)));
  }

  @Test
  void shouldReturnInternalServerErrorWhenParsingTransactionsFromEmptyFile() throws Exception {
    // given
    final String path = "src/test/resources/csv/ing/emptyFile.csv";
    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(TARGET_BANK_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    // when
    int status = callRestToImportTransactionsFromCsvFileAndReturnStatus(importTargetAccountId, mockMultipartFile);

    // then
    assertThat(status, is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
  }

  @Test
  void shouldNotAddAnyTransactionsWhenImportingTransactionsAgainFromTheSameFile() throws Exception {
    // given
    final String path = "src/test/resources/csv/ing/original.csv";

    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(TARGET_BANK_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    List<Transaction> filteredTransactions = callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile,
        importTargetAccountId);
    final List<Transaction> allTransactionsFromDbAfterFirstFileImport = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(filteredTransactions.size(), is(equalTo(251)));
    assertThat(allTransactionsFromDbAfterFirstFileImport.size(), is(equalTo(251)));

    // when
    List<Transaction> filteredTransactionsAfterAnotherAttemptOfParsingTheSameCsvFile =
        callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile, importTargetAccountId);
    final List<Transaction> allTransactionsFromDbAfterSecondImportTheSameFile = callRestToGetAllTransactionsFromDatabase(token);

    // then
    assertThat(filteredTransactionsAfterAnotherAttemptOfParsingTheSameCsvFile.size(), is(equalTo(0)));
    assertThat(allTransactionsFromDbAfterSecondImportTheSameFile.size(), is(equalTo(251)));
    assertThat(allTransactionsFromDbAfterSecondImportTheSameFile, is(equalTo(allTransactionsFromDbAfterFirstFileImport)));
  }

  @Test
  void shouldCreateImportCategoryDuringTransactionsImportWhenUserDeletedImportCategory() throws Exception {
    // given
    final String path = "src/test/resources/csv/ing/original.csv";
    final long categoryNamedImportedId = callRestToGetCategoryNamedImportedId(token);
    final List<Category> allCategories = callRestToGetAllCategories(token);
    assertThat(allCategories.size(), is(equalTo(1)));
    assertThat(allCategories.get(0).getName(), is(equalTo("Imported")));

    callRestToDeleteCategoryById(categoryNamedImportedId, token);

    final List<Category> allCategoriesAfterDeletion = callRestToGetAllCategories(token);

    assertThat(allCategoriesAfterDeletion.size(), is(equalTo(0)));

    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(TARGET_BANK_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Credit").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    List<Transaction> filteredTransactions = callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile,
        importTargetAccountId);
    final List<Transaction> allTransactionsFromDbAfterFirstFileImport = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(filteredTransactions.size(), is(equalTo(251)));
    assertThat(allTransactionsFromDbAfterFirstFileImport.size(), is(equalTo(251)));

    // when
    List<Transaction> filteredTransactionsAfterAnotherAttemptOfParsingTheSameCsvFile =
        callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile, importTargetAccountId);

    // then
    final List<Category> allCategoriesAfterCsvTransactionsImport = callRestToGetAllCategories(token);
    assertThat(allCategoriesAfterCsvTransactionsImport.size(), is(equalTo(1)));
    assertThat(allCategoriesAfterCsvTransactionsImport.get(0).getName(), is(equalTo("Imported")));

    final List<Transaction> allTransactionsFromDbAfterSecondImportTheSameFile = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(filteredTransactionsAfterAnotherAttemptOfParsingTheSameCsvFile.size(), is(equalTo(0)));
    assertThat(allTransactionsFromDbAfterSecondImportTheSameFile.size(), is(equalTo(251)));
    assertThat(allTransactionsFromDbAfterSecondImportTheSameFile, is(equalTo(allTransactionsFromDbAfterFirstFileImport)));
  }

  @Test
  void shouldUpdateAccountsBalancesForOwnTransfer() throws Exception {
    // given
    final String path = "src/test/resources/csv/ing/1_own_transfer.csv";

    Account importTargetAccount = Account.builder()
        .name("Main")
        .bankAccountNumber(ING_ACCOUNT_NUMBER)
        .type(AccountType.builder().id(16L).name("Main").build())
        .balance(convertDoubleToBigDecimal(1000))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long importTargetAccountId = callRestServiceToAddAccountAndReturnId(importTargetAccount, token);

    Account ingSubAccount = Account.builder()
        .name("Sub")
        .bankAccountNumber(ING_SUB_ACCOUNT_NUMBER1)
        .type(AccountType.builder().id(16L).name("Sub").build())
        .balance(convertDoubleToBigDecimal(100))
        .currency(Currency.builder().id(13L).name("Pln").exchangeRate(BigDecimal.valueOf(1.00)).build())
        .build();
    final long ingSubAccountId = callRestServiceToAddAccountAndReturnId(ingSubAccount, token);

    final List<Transaction> beforeImportTransactions = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(beforeImportTransactions.size(), is(equalTo(0)));

    final byte[] fileBytes = Files.readAllBytes(Paths.get(path));
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file", TEXT_CSV, fileBytes);

    // when
    final List<Transaction> filteredTransactions = callRestToImportTransactionsFromCsvFileAndReturnFilteredTransactions(mockMultipartFile,
        importTargetAccountId);
    final BigDecimal mainAccountBalance = callRestServiceAndReturnAccountBalance(importTargetAccountId, token);
    final BigDecimal subAccountBalance = callRestServiceAndReturnAccountBalance(ingSubAccountId, token);

    assertThat(mainAccountBalance, is(equalTo(BigDecimal.valueOf(999.21))));
    assertThat(subAccountBalance, is(equalTo(BigDecimal.valueOf(100.79))));

    final List<Transaction> afterImportTransactions = callRestToGetAllTransactionsFromDatabase(token);
    assertThat(afterImportTransactions.size(), is(equalTo(1)));

    // then
    assertThat(filteredTransactions.size(), is(equalTo(1)));
  }
}
