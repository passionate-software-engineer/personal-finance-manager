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
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestTransactionProvider;
import com.pfm.history.HistoryEntry.Type;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String HISTORY_PATH = "/history";

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldReturnHistoryOfAddingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();
    callRestServiceToAddAccountAndReturnId(account, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

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

    assertThat(historyEntries, hasSize(1));
    assertThat(historyEntries.get(0).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(0).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();
    Account updatedAccount = accountMbankBalance10();
    updatedAccount.setName("updatedName");
    updatedAccount.setBalance(convertDoubleToBigDecimal(999));

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToUpdateAccount(accountId, convertAccountToAccountRequest(updatedAccount), token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("name")
        .newValue(updatedAccount.getName())
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("balance")
        .newValue(updatedAccount.getBalance().toString())
        .oldValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfDeletingAccount() throws Exception {

    //given
    Account account = accountMbankBalance10();

    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);
    callRestToDeleteAccountById(accountId, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(3L)
        .name("name")
        .oldValue(account.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("balance")
        .oldValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
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

    assertThat(historyEntries, hasSize(1));
    assertThat(historyEntries.get(0).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
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
        .id(3L)
        .name("name")
        .newValue(category.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("parentCategory")
        .newValue(parentCategory.getName())
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(0).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(0).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(0).getUserId(), equalTo(userId));
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

    callRestToUpdateCategory(categoryId, convertCategoryToCategoryRequest(updatedCategory), token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then
    List<HistoryInfo> historyInfosExpected = new ArrayList<>();

    historyInfosExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("name")
        .oldValue(category.getName())
        .newValue(updatedCategory.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("parentCategory")
        .oldValue(parentCategory.getName())
        .newValue(getMessage(MAIN_CATEGORY))
        .build());

    assertThat(historyEntries, hasSize(3));
    assertThat(historyEntries.get(2).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
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
        .id(3L)
        .name("name")
        .oldValue(category.getName())
        .build());

    historyInfosExpected.add(HistoryInfo.builder()
        .id(4L)
        .name("parentCategory")
        .oldValue(getMessage(MAIN_CATEGORY))
        .build());

    assertThat(historyEntries, hasSize(2));
    assertThat(historyEntries.get(1).getObject(), equalTo(Category.class.getSimpleName()));
    assertThat(historyEntries.get(1).getType(), equalTo(Type.DELETE));
    assertThat(historyEntries.get(1).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(1).getEntries(), equalTo(historyInfosExpected));
  }

  @Test
  public void shouldReturnHistoryOfAddingTransaction() throws Exception {

    //given
    Category category = categoryCar();
    final long categoryId = callRestToAddCategoryAndReturnId(category, token);

    Account account = accountMilleniumBalance100();
    final long accountId = callRestServiceToAddAccountAndReturnId(account, token);

    Transaction transaction = TestTransactionProvider.carTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transaction, accountId, categoryId, token);

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then

    List<HistoryInfo> historyInfosOfAddingTransactionExpected = new ArrayList<>();

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("description")
        .newValue(transaction.getDescription())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("categoryId")
        .newValue(category.getName())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("date")
        .newValue(transaction.getDate().toString())
        .build());

    historyInfosOfAddingTransactionExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", account.getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    List<HistoryInfo> historyInfosOfUpdatingAccountExpected = new ArrayList<>();

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(5L)
        .name("name")
        .newValue(account.getName())
        .oldValue(account.getName())
        .build());

    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
        .id(6L)
        .name("balance")
        .newValue(account.getBalance().add(transaction.getAccountPriceEntries().get(0).getPrice()).toString())
        .oldValue(account.getBalance().toString())
        .build());

    assertThat(historyEntries, hasSize(4));
    assertThat(historyEntries.get(2).getObject(), equalTo(Account.class.getSimpleName()));
    assertThat(historyEntries.get(2).getType(), equalTo(Type.UPDATE));
    assertThat(historyEntries.get(2).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(2).getEntries(), equalTo(historyInfosOfUpdatingAccountExpected));
    assertThat(historyEntries.get(3).getObject(), equalTo(Transaction.class.getSimpleName()));
    assertThat(historyEntries.get(3).getType(), equalTo(Type.ADD));
    assertThat(historyEntries.get(3).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(3).getEntries(), equalTo(historyInfosOfAddingTransactionExpected));
  }

  @Test
  public void shouldReturnHistoryOfUpdatingTransaction() throws Exception {

    //given
    final long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    final long categoryFoodId = callRestToAddCategoryAndReturnId(categoryFood(), token);

    final long accountIdeaId = callRestServiceToAddAccountAndReturnId(accountIdeaBalance100000(), token);
    final long accountIngId = callRestServiceToAddAccountAndReturnId(accountIngBalance9999(), token);

    Transaction transaction = TestTransactionProvider.carTransactionWithNoAccountAndNoCategory();
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

    mockMvc.perform(put(TRANSACTIONS_SERVICE_PATH + "/" + transactionId)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertTransactionToTransactionRequest(updatedTransaction))))
        .andExpect(status()
            .isOk());

    //when
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);

    //then

    List<HistoryInfo> historyInfosOfUpdatingTransactionExpected = new ArrayList<>();

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(7L)
        .name("description")
        .oldValue(transaction.getDescription())
        .newValue(updatedTransaction.getDescription())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(8L)
        .name("categoryId")
        .oldValue(categoryCar().getName())
        .newValue(categoryFood().getName())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(9L)
        .name("date")
        .oldValue(transaction.getDate().toString())
        .newValue(updatedTransaction.getDate().toString())
        .build());

    historyInfosOfUpdatingTransactionExpected.add(HistoryInfo.builder()
        .id(10L)
        .name("accountPriceEntries")
        .oldValue(String.format("[%s : %s]", accountIdeaBalance100000().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .newValue(String.format("[%s : %s, %s : %s]", accountIdeaBalance100000().getName(), "55.00", accountIngBalance9999().getName(), "65.00"))
        .build());

//    List<HistoryInfo> historyInfosOfUpdatingAccountExpected = new ArrayList<>();
//
//    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
//        .id(5L)
//        .name("name")
//        .newValue(account.getName())
//        .oldValue(account.getName())
//        .build());
//
//    historyInfosOfUpdatingAccountExpected.add(HistoryInfo.builder()
//        .id(6L)
//        .name("balance")
//        .newValue(account.getBalance().add(transaction.getAccountPriceEntries().get(0).getPrice()).toString())
//        .oldValue(account.getBalance().toString())
//        .build());

    assertThat(historyEntries, hasSize(10));
//    assertThat(historyEntries.get(6).getObject(), equalTo(Account.class.getSimpleName()));
//    assertThat(historyEntries.get(6).getType(), equalTo(Type.UPDATE));
//    assertThat(historyEntries.get(6).getUserId(), equalTo(userId));
//    assertThat(historyEntries.get(6).getEntries(), equalTo(historyInfosOfUpdatingAccountExpected));
//    assertThat(historyEntries.get(7).getObject(), equalTo(Account.class.getSimpleName()));
//    assertThat(historyEntries.get(7).getType(), equalTo(Type.UPDATE));
//    assertThat(historyEntries.get(7).getUserId(), equalTo(userId));
//    assertThat(historyEntries.get(7).getEntries(), equalTo(historyInfosOfUpdatingAccountExpected));
//    assertThat(historyEntries.get(6).getObject(), equalTo(Transaction.class.getSimpleName()));
//    assertThat(historyEntries.get(6).getType(), equalTo(Type.UPDATE));
//    assertThat(historyEntries.get(6).getUserId(), equalTo(userId));
    assertThat(historyEntries.get(9).getEntries(), equalTo(historyInfosOfUpdatingTransactionExpected));
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

  private List<List<HistoryInfo>> callRestServiceToReturnHistoryInfos(String token) throws Exception {
    List<HistoryEntry> historyEntries = callRestServiceToReturnHistoryEntries(token);
    return historyEntries.stream()
        .map(HistoryEntry::getEntries)
        .collect(Collectors.toList());
  }

  private List<HistoryEntry> getHistoryEntriesFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, HistoryEntry.class));
  }

}