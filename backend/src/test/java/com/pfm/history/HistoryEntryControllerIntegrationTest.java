package com.pfm.history;

import static com.pfm.config.MessagesProvider.MAIN_CATEGORY;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountIdeaBalance100000;
import static com.pfm.helpers.TestAccountProvider.accountIngBalance9999;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestAccountProvider.accountMilleniumBalance100;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestFilterProvider.convertIdsToList;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carPlannedTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.filter.Filter;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.history.HistoryEntry.Type;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String HISTORY_PATH = "/history";

  @BeforeEach
  public void beforeEach() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldReturnHistoryOfAddingAccount() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    callRestServiceToAddAccountAndReturnId(account, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(1L)
        .name("name")
        .newValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(2L)
        .name("balance")
        .newValue(account.getBalance().toString())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("archived")
        .newValue(String.valueOf(account.isArchived()))
        .oldValue(null)
        .build());

    assertThat(historyEntries, hasSize(1));
    assertThat(historyEntries.get(0).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(0).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(0).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingAccount() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setName("updatedName");
    updatedAccount.setBalance(convertDoubleToBigDecimal(999));
    updatedAccount.setCurrency(currencyService.getCurrencies(userId).get(1));
    updatedAccount.setType(accountTypeService.getAccountTypes(userId).get(1));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToUpdateAccount(accountId, convertAccountToAccountRequest(updatedAccount), token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("name")
        .newValue(updatedAccount.getName())
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("balance")
        .newValue(updatedAccount.getBalance().toString())
        .oldValue(account.getBalance().toString())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("archived")
        .newValue(String.valueOf(updatedAccount.isArchived()))
        .oldValue(String.valueOf(updatedAccount.isArchived()))
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(1).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));

    // TODO currency change assertion
  }

  @Test
  public void shouldReturnHistoryOfDeletingAccount() throws Exception {
    //given
    Account account = accountMbankBalance10();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToDeleteAccountById(accountId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("name")
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("balance")
        .oldValue(account.getBalance().toString())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("archived")
        .oldValue("false")
        .newValue(null)
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(1).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfAddingCategoryWithNoParentCategory() throws Exception {
    //given
    Category category = categoryOil();
    callRestToAddCategoryAndReturnId(category, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(1L)
        .name("name")
        .newValue(category.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(2L)
        .name("parentCategory")
        .newValue(getMessage(MAIN_CATEGORY))
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("priority")
        .newValue(String.valueOf(category.getPriority()))
        .build());

    assertThat(historyEntries, hasSize(1));
    assertThat(historyEntries.get(0).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(0).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(0).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfAddingCategoryWithParentCategory() throws Exception {
    //given
    Category category = categoryOil();
    Category parentCategory = categoryCar();
    final long parentCategoryId = callRestToAddCategoryAndReturnId(parentCategory, token);
    category.setParentCategory(Category.builder()
        .id(parentCategoryId)
        .build());

    callRestToAddCategoryAndReturnId(category, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("name")
        .newValue(category.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("parentCategory")
        .newValue(parentCategory.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("priority")
        .newValue(String.valueOf(category.getPriority()))
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(0).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(0).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingCategory() throws Exception {
    //given
    Category category = categoryOil();
    Category parentCategory = categoryCar();

    final long parentCategoryId = callRestToAddCategoryAndReturnId(parentCategory, token);

    category.setParentCategory(Category.builder()
        .id(parentCategoryId)
        .build());

    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Category updatedCategory = categoryOil();
    updatedCategory.setName("Brakes oil");
    updatedCategory.setPriority(5);

    callRestToUpdateCategory(categoryId, convertCategoryToCategoryRequest(updatedCategory), token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("name")
        .oldValue(category.getName())
        .newValue(updatedCategory.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("parentCategory")
        .oldValue(parentCategory.getName())
        .newValue(getMessage(MAIN_CATEGORY))
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("priority")
        .oldValue(String.valueOf(category.getPriority()))
        .newValue(String.valueOf(updatedCategory.getPriority()))
        .build());

    assertThat(historyEntries, hasSize(3));
    assertThat(historyEntries.get(2).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(2).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(2).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingCategory() throws Exception {
    //given
    Category category = categoryOil();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    callRestToDeleteCategoryById(categoryId, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("name")
        .oldValue(category.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("parentCategory")
        .oldValue(getMessage(MAIN_CATEGORY))
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("priority")
        .oldValue(String.valueOf(category.getPriority()))
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(1).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfAddingTransaction() throws Exception {
    //given
    Category category = categoryCar();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Account account = accountMilleniumBalance100();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Transaction transaction = carTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transaction, accountId, categoryId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then

    List<HistoryInfo> historyInfosOfAddingTransactionExpected = new ArrayList<>();

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("description")
        .newValue(transaction.getDescription())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(11L)
        .name("categoryId")
        .newValue(category.getName())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(12L)
        .name("date")
        .newValue(transaction.getDate().toString())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(13L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", account.getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("name")
        .newValue(account.getName())
        .oldValue(account.getName())
        .build());

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("balance")
        .newValue(account.getBalance().add(transaction.getAccountPriceEntries().get(0).getPrice()).toString())
        .oldValue(account.getBalance().toString())
        .build());

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("archived")
        .newValue(String.valueOf(account.isArchived()))
        .oldValue(String.valueOf(account.isArchived()))
        .build());

    assertThat(historyEntries, hasSize(4));

    assertThat(historyEntries.get(2).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(2).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(2).getEntries(), equalTo(historyInfosOfUpdatingAccountExpected));

    assertThat(historyEntries.get(3).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(3).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(3).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(3).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(3).getEntries(), equalTo(historyInfosOfAddingTransactionExpected));
  }

  @Test
  public void shouldReturnHistoryOfAddingPlannedTransaction() throws Exception {
    //given
    Category category = categoryCar();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Account account = accountMilleniumBalance100();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Transaction plannedTransaction = carPlannedTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(plannedTransaction, accountId, categoryId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosOfAddingTransactionExpected = new ArrayList<>();

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("description")
        .newValue(plannedTransaction.getDescription())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("categoryId")
        .newValue(category.getName())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("date")
        .newValue(plannedTransaction.getDate().toString())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", account.getName(), plannedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyEntries, hasSize(3));

    assertThat(historyEntries.get(2).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(2).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(2).getEntries(), equalTo(historyInfosOfAddingTransactionExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingPlannedTransaction() throws Exception {
    //given
    Category category = categoryCar();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Account account = accountMilleniumBalance100();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Transaction plannedTransaction = carPlannedTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(plannedTransaction, accountId, categoryId, token);
    callRestToDeleteTransactionById(transactionId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    final List<HistoryInfo> historyInfosOfDeletingTransactionExpected = new ArrayList<>();
    List<HistoryInfo> historyInfosOfAddingTransactionExpected = new ArrayList<>();

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("description")
        .newValue(plannedTransaction.getDescription())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("categoryId")
        .newValue(category.getName())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("date")
        .newValue(plannedTransaction.getDate().toString())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", account.getName(), plannedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(11L)
        .name("description")
        .oldValue(plannedTransaction.getDescription())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(12L)
        .name("categoryId")
        .oldValue(category.getName())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(13L)
        .name("date")
        .oldValue(plannedTransaction.getDate().toString())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(14L)
        .name("accountPriceEntries")
        .oldValue(String.format("[%s : %s]", account.getName(), plannedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyEntries, hasSize(4));

    assertThat(historyEntries.get(2).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(2).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(2).getEntries(), equalTo(historyInfosOfAddingTransactionExpected));

    assertThat(historyEntries.get(3).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(3).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(3).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(3).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(3).getEntries(), equalTo(historyInfosOfDeletingTransactionExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingTransaction() throws Exception {
    //given
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Account accountIdea = accountIdeaBalance100000();
    accountIdea.setCurrency(currencyService.getCurrencies(userId).get(0));
    accountIdea.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountIdeaId = callRestServiceToAddAccountAndReturnId(accountIdea, token);

    Account accountIng = accountIngBalance9999();
    accountIng.setCurrency(currencyService.getCurrencies(userId).get(1));
    accountIng.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountIngId = callRestServiceToAddAccountAndReturnId(accountIng, token);

    Transaction transaction = carTransactionWithNoAccountAndNoCategory();
    final long transactionId = callRestToAddTransactionAndReturnId(transaction, accountIdeaId, categoryCarId, token);

    List<AccountPriceEntry> accountPriceEntriesUpdated = new ArrayList<>();
    accountPriceEntriesUpdated.add(AccountPriceEntry.builder()
        .accountId(accountIdeaId)
        .price(convertDoubleToBigDecimal(65))
        .build());
    accountPriceEntriesUpdated.add(AccountPriceEntry.builder()
        .accountId(accountIngId)
        .price(convertDoubleToBigDecimal(55))
        .build());

    Transaction updatedTransaction = Transaction.builder()
        .description("updatedName")
        .categoryId(categoryFoodId)
        .date(LocalDate.of(2018, 11, 13))
        .accountPriceEntries(accountPriceEntriesUpdated)
        .build();

    callRestToUpdateTransactionAndReturnCommitResult(transactionId, helper.convertTransactionToTransactionRequest(updatedTransaction), token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosOfUpdatingTransactionExpected = new ArrayList<>();

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(20L)
        .name("description")
        .oldValue(transaction.getDescription())
        .newValue(updatedTransaction.getDescription())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(21L)
        .name("categoryId")
        .oldValue(categoryCar().getName())
        .newValue(categoryFood().getName())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(22L)
        .name("date")
        .oldValue(transaction.getDate().toString())
        .newValue(updatedTransaction.getDate().toString())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(23L)
        .name("accountPriceEntries")
        .oldValue(String.format("[%s : %s]", accountIdeaBalance100000().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .newValue(String
            .format("[%s : %s, %s : %s]", accountIdeaBalance100000().getName(), updatedTransaction.getAccountPriceEntries().get(0).getPrice(),
                accountIngBalance9999().getName(), updatedTransaction.getAccountPriceEntries().get(1).getPrice()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountIdeaSubstractAmountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountIdeaSubstractAmountExpected.add(HistoryInfo.builder()
        .id(24L)
        .name("name")
        .newValue(accountIdeaBalance100000().getName())
        .oldValue(accountIdeaBalance100000().getName())
        .build());

    historyInfosOfUpdatingAccountIdeaSubstractAmountExpected.add(HistoryInfo.builder()
        .id(25L)
        .name("balance")
        .newValue(accountIdeaBalance100000().getBalance().toString())
        .oldValue(accountIdeaBalance100000().getBalance().add(transaction.getAccountPriceEntries().get(0).getPrice()).toString())
        .build());

    historyInfosOfUpdatingAccountIdeaSubstractAmountExpected.add(HistoryInfo.builder()
        .id(26L)
        .name("archived")
        .newValue(String.valueOf(accountIdeaBalance100000().isArchived()))
        .oldValue(String.valueOf(accountIdeaBalance100000().isArchived()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountIdeaAddAmountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountIdeaAddAmountExpected.add(HistoryInfo.builder()
        .id(27L)
        .name("name")
        .newValue(accountIdeaBalance100000().getName())
        .oldValue(accountIdeaBalance100000().getName())
        .build());

    historyInfosOfUpdatingAccountIdeaAddAmountExpected.add(HistoryInfo.builder()
        .id(28L)
        .name("balance")
        .oldValue(accountIdeaBalance100000().getBalance().toString())
        .newValue(accountIdeaBalance100000().getBalance().add(updatedTransaction.getAccountPriceEntries().get(0).getPrice()).toString())
        .build());

    historyInfosOfUpdatingAccountIdeaAddAmountExpected.add(HistoryInfo.builder()
        .id(29L)
        .name("archived")
        .newValue(String.valueOf(accountIdeaBalance100000().isArchived()))
        .oldValue(String.valueOf(accountIdeaBalance100000().isArchived()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountIngAddAmountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountIngAddAmountExpected.add(HistoryInfo.builder()
        .id(30L)
        .name("name")
        .newValue(accountIngBalance9999().getName())
        .oldValue(accountIngBalance9999().getName())
        .build());

    historyInfosOfUpdatingAccountIngAddAmountExpected.add(HistoryInfo.builder()
        .id(31L)
        .name("balance")
        .oldValue(accountIngBalance9999().getBalance().toString())
        .newValue(accountIngBalance9999().getBalance().add(updatedTransaction.getAccountPriceEntries().get(1).getPrice()).toString())
        .build());

    historyInfosOfUpdatingAccountIngAddAmountExpected.add(HistoryInfo.builder()
        .id(32L)
        .name("archived")
        .newValue(String.valueOf(accountIngBalance9999().isArchived()))
        .oldValue(String.valueOf(accountIngBalance9999().isArchived()))
        .build());

    assertThat(historyEntries, hasSize(10));

    assertThat(historyEntries.get(6).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(6).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(6).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(6).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(6).getEntries(), equalTo(historyInfosOfUpdatingTransactionExpected));

    assertThat(historyEntries.get(7).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(7).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(7).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(7).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(7).getEntries(), equalTo(historyInfosOfUpdatingAccountIdeaSubstractAmountExpected));

    assertThat(historyEntries.get(8).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(8).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(8).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(8).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(8).getEntries(), equalTo(historyInfosOfUpdatingAccountIdeaAddAmountExpected));

    assertThat(historyEntries.get(9).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(9).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(9).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(9).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(9).getEntries(), equalTo(historyInfosOfUpdatingAccountIngAddAmountExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingPlannedTransaction() throws Exception {
    //given
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    Account accountIdea = accountIdeaBalance100000();
    accountIdea.setCurrency(currencyService.getCurrencies(userId).get(0));
    accountIdea.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountIdeaId = callRestServiceToAddAccountAndReturnId(accountIdea, token);

    Account accountIng = accountIngBalance9999();
    accountIng.setCurrency(currencyService.getCurrencies(userId).get(1));
    accountIng.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountIngId = callRestServiceToAddAccountAndReturnId(accountIng, token);

    Transaction transaction = carPlannedTransactionWithNoAccountAndNoCategory();
    final long transactionId = callRestToAddTransactionAndReturnId(transaction, accountIdeaId, categoryCarId, token);

    List<AccountPriceEntry> accountPriceEntriesUpdated = new ArrayList<>();
    accountPriceEntriesUpdated.add(AccountPriceEntry.builder()
        .accountId(accountIngId)
        .price(convertDoubleToBigDecimal(65))
        .build());

    Transaction updatedTransaction = Transaction.builder()
        .description("updatedName")
        .categoryId(categoryFoodId)
        .date(LocalDate.now().plusDays(12))
        .accountPriceEntries(accountPriceEntriesUpdated)
        .isPlanned(true)
        .build();

    callRestToUpdateTransactionAndReturnCommitResult(transactionId, helper.convertTransactionToTransactionRequest(updatedTransaction), token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    final List<HistoryInfo> historyInfosOfUpdatingTransactionExpected = new ArrayList<>();
    final List<HistoryInfo> historyInfosOfAddingTransactionExpected = new ArrayList<>();

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(13L)
        .name("description")
        .newValue("Oil")
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(14L)
        .name("categoryId")
        .newValue("Car")
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(15L)
        .name("date")
        .newValue(String.valueOf(LocalDate.now().plusDays(2)))
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(16L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", accountIdeaBalance100000().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(17L)
        .name("description")
        .newValue("updatedName")
        .oldValue("Oil")
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(18L)
        .name("categoryId")
        .newValue("Food")
        .oldValue("Car")
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(19L)
        .name("date")
        .newValue(String.valueOf(updatedTransaction.getDate()))
        .oldValue(String.valueOf(transaction.getDate()))
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(20L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", accountIngBalance9999().getName(), updatedTransaction.getAccountPriceEntries().get(0).getPrice()))
        .oldValue(String.format("[%s : %s]", accountIdeaBalance100000().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    assertThat(historyEntries, hasSize(6));

    assertThat(historyEntries.get(4).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(4).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(4).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(4).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(4).getEntries(), equalTo(historyInfosOfAddingTransactionExpected));

    assertThat(historyEntries.get(5).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(5).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(5).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(5).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(5).getEntries(), equalTo(historyInfosOfUpdatingTransactionExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingTransaction() throws Exception {
    //given
    Category category = categoryCar();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Account account = accountMilleniumBalance100();
    account.setCurrency(currencyService.getCurrencies(userId).get(1));
    account.setType(accountTypeService.getAccountTypes(userId).get(0));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Transaction transaction = carTransactionWithNoAccountAndNoCategory();
    final long transactionId = callRestToAddTransactionAndReturnId(transaction, accountId, categoryId, token);
    callRestToDeleteTransactionById(transactionId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosOfDeletingTransactionExpected = new ArrayList<>();

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(17L)
        .name("description")
        .oldValue(transaction.getDescription())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(18L)
        .name("categoryId")
        .oldValue(category.getName())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(19L)
        .name("date")
        .oldValue(transaction.getDate().toString())
        .build());

    historyInfosOfDeletingTransactionExpected.add(HistoryInfo.builder()
        .id(20L)
        .name("accountPriceEntries")
        .oldValue(String.format("[%s : %s]", account.getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(14L)
        .name("name")
        .newValue(account.getName())
        .oldValue(account.getName())
        .build());

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(15L)
        .name("balance")
        .oldValue(account.getBalance().add(transaction.getAccountPriceEntries().get(0).getPrice()).toString())
        .newValue(account.getBalance().toString())
        .build());

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(16L)
        .name("archived")
        .oldValue(String.valueOf(account.isArchived()))
        .newValue(String.valueOf(account.isArchived()))
        .build());

    assertThat(historyEntries, hasSize(6));

    assertThat(historyEntries.get(4).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(4).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(4).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(4).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(4).getEntries(), equalTo(historyInfosOfUpdatingAccountExpected));

    assertThat(historyEntries.get(5).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(5).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(5).getUserId(), equalTo(userId));
    assertTrue(historyEntries.get(5).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(5).getEntries(), equalTo(historyInfosOfDeletingTransactionExpected));

  }

  @Test
  public void shouldReturnHistoryOfAddingFilter() throws Exception {
    //given
    Category categoryCar = categoryCar();
    Category categoryFood = categoryFood();
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar, token);
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood, token);

    Account accountMillenium = accountMilleniumBalance100();
    accountMillenium.setCurrency(currencyService.getCurrencies(userId).get(0));
    accountMillenium.setType(accountTypeService.getAccountTypes(userId).get(0));

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(1));
    accountMbank.setType(accountTypeService.getAccountTypes(userId).get(1));

    final long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbank, token);
    final long accountMilleniumId = callRestServiceToAddAccountAndReturnId(accountMillenium, token);

    Filter filter = filterFoodExpenses();
    filter.setAccountIds(convertIdsToList(accountMbankId, accountMilleniumId));
    filter.setCategoryIds(convertIdsToList(categoryCarId, categoryFoodId));
    filter.setDateTo(null);

    callRestServiceToAddFilterAndReturnId(convertFilterToFilterRequest(filter), token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    List<HistoryInfo> historyInfosOfAddingFilterExpected = new ArrayList<>();

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(13L)
        .name("name")
        .newValue(filter.getName())
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(14L)
        .name("accountIds")
        .newValue(String.format("[%s, %s]", accountMbank.getName(), accountMillenium.getName()))
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(15L)
        .name("categoryIds")
        .newValue(String.format("[%s, %s]", categoryCar.getName(), categoryFood.getName()))
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(16L)
        .name("priceFrom")
        .newValue(filter.getPriceFrom().toString())
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(17L)
        .name("priceTo")
        .newValue(filter.getPriceTo().toString())
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(18L)
        .name("dateFrom")
        .newValue(filter.getDateFrom().toString())
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(19L)
        .name("dateTo")
        .newValue(null)
        .build());

    historyInfosOfAddingFilterExpected.add(HistoryInfo.builder()
        .id(20L)
        .name("description")
        .newValue(filter.getDescription())
        .build());

    assertThat(historyEntries, hasSize(5));
    assertThat(historyEntries.get(4).getObject(), equalTo(Filter.class.getSimpleName()));
    assertThat(historyEntries.get(4).getType(), equalTo(Type.ADD));
    assertTrue(historyEntries.get(4).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(4).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(4).getEntries(), equalTo(historyInfosOfAddingFilterExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingFilter() throws Exception {
    //given
    Category categoryCar = categoryCar();
    Category categoryFood = categoryFood();
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar, token);
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood, token);

    Account accountMillenium = accountMilleniumBalance100();
    accountMillenium.setCurrency(currencyService.getCurrencies(userId).get(0));
    accountMillenium.setType(accountTypeService.getAccountTypes(userId).get(0));

    Account accountMbank = accountMbankBalance10();
    accountMbank.setCurrency(currencyService.getCurrencies(userId).get(1));
    accountMbank.setType(accountTypeService.getAccountTypes(userId).get(1));

    final long accountMbankId = callRestServiceToAddAccountAndReturnId(accountMbank, token);
    final long accountMilleniumId = callRestServiceToAddAccountAndReturnId(accountMillenium, token);

    Filter filter = filterFoodExpenses();
    final long filterId = callRestServiceToAddFilterAndReturnId(convertFilterToFilterRequest(filter), token);

    Filter updatedFilter = Filter.builder()
        .name("updated name")
        .accountIds(convertIdsToList(accountMbankId, accountMilleniumId))
        .categoryIds(convertIdsToList(categoryCarId, categoryFoodId))
        .priceFrom(filter.getPriceFrom().add(BigDecimal.ONE))
        .priceTo(filter.getPriceTo().add(BigDecimal.ONE))
        .dateFrom(filter.getDateFrom().plusDays(1))
        .dateTo(filter.getDateTo().plusDays(1))
        .description("updated description")
        .build();

    callRestServiceToUpdateFilter(filterId, convertFilterToFilterRequest(updatedFilter), token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    List<HistoryInfo> historyInfosOfUpdatingFilterExpected = new ArrayList<>();

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(21L)
        .name("name")
        .oldValue(filter.getName())
        .newValue(updatedFilter.getName())
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(22L)
        .name("accountIds")
        .newValue(String.format("[%s, %s]", accountMbank.getName(), accountMillenium.getName()))
        .oldValue("[]")
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(23L)
        .name("categoryIds")
        .newValue(String.format("[%s, %s]", categoryCar.getName(), categoryFood.getName()))
        .oldValue("[]")
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(24L)
        .name("priceFrom")
        .oldValue(filter.getPriceFrom().toString())
        .newValue(updatedFilter.getPriceFrom().toString())
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(25L)
        .name("priceTo")
        .newValue(updatedFilter.getPriceTo().toString())
        .oldValue(filter.getPriceTo().toString())
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(26L)
        .name("dateFrom")
        .oldValue(filter.getDateFrom().toString())
        .newValue(updatedFilter.getDateFrom().toString())
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(27L)
        .name("dateTo")
        .newValue(updatedFilter.getDateTo().toString())
        .oldValue(filter.getDateTo().toString())
        .build());

    historyInfosOfUpdatingFilterExpected.add(HistoryInfo.builder()
        .id(28L)
        .name("description")
        .oldValue(filter.getDescription())
        .newValue(updatedFilter.getDescription())
        .build());

    assertThat(historyEntries, hasSize(6));
    assertThat(historyEntries.get(5).getObject(), equalTo(Filter.class.getSimpleName()));
    assertThat(historyEntries.get(5).getType(), equalTo(Type.UPDATE));
    assertTrue(historyEntries.get(5).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(5).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(5).getEntries(), equalTo(historyInfosOfUpdatingFilterExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingFilter() throws Exception {
    //given
    Filter filter = filterFoodExpenses();
    final long filterId = callRestServiceToAddFilterAndReturnId(convertFilterToFilterRequest(filter), token);

    callRestToDeleteFilterById(filterId, token);

    //when
    final List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    List<HistoryInfo> historyInfosOfDeletingFilterExpected = new ArrayList<>();

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("name")
        .oldValue(filter.getName())
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("accountIds")
        .oldValue("[]")
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(11L)
        .name("categoryIds")
        .oldValue("[]")
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(12L)
        .name("priceFrom")
        .oldValue(filter.getPriceFrom().toString())
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(13L)
        .name("priceTo")
        .oldValue(filter.getPriceTo().toString())
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(14L)
        .name("dateFrom")
        .oldValue(filter.getDateFrom().toString())
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(15L)
        .name("dateTo")
        .oldValue(filter.getDateTo().toString())
        .build());

    historyInfosOfDeletingFilterExpected.add(HistoryInfo.builder()
        .id(16L)
        .name("description")
        .oldValue(filter.getDescription())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Filter.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertTrue(historyEntries.get(1).getDate().isAfter(ZonedDateTime.now().minusMinutes(2)));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosOfDeletingFilterExpected));
  }

  private List<HistoryEntry> callRestServiceToReturnHistoryEntries(String token) throws Exception {
    String response =
        mockMvc
            .perform(get(HISTORY_PATH)
                .header(HttpHeaders.AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    return getHistoryEntriesFromResponse(response);
  }

  private List<HistoryEntry> getHistoryEntriesFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, HistoryEntry.class));
  }

}
