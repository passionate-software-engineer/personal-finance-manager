package com.pfm.history;

import static com.pfm.helpers.BigDecimalHelper.convertBigDecimalToString;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static com.pfm.history.DifferenceProvider.ENTRY_VALUES_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.category.Category;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestCategoryProvider;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HistoryEntryControllerIntegrationTest extends IntegrationTestsBase {

  private static final String HISTORY_PATH = "/history";

  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldGetAllHistoryEntries() throws Exception {

    //given
    long accountId = callRestServiceToAddAccountAndReturnId(accountMbankBalance10(), token);

    final List<String> expectedHistoryEntryAddAccount = new ArrayList<>();
    expectedHistoryEntryAddAccount.add("Added Account");
    expectedHistoryEntryAddAccount.add(String.format(ENTRY_VALUES_TEMPLATE, "name", accountMbankBalance10().getName()));
    expectedHistoryEntryAddAccount
        .add(String.format(ENTRY_VALUES_TEMPLATE, "balance", convertBigDecimalToString(accountMbankBalance10().getBalance())));

    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    Category categoryOil = TestCategoryProvider.categoryOil();
    categoryOil.setParentCategory(Category.builder().id(categoryCarId).build());
    long categoryOilId = callRestToAddCategoryAndReturnId(categoryOil, token);

    List<String> expectedAddedCategoryCarHistoryEntry = new ArrayList<>();
    expectedAddedCategoryCarHistoryEntry.add("Added Category");
    expectedAddedCategoryCarHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "name", categoryCar().getName()));

    List<String> expectedAddedCategoryOilHistoryEntry = new ArrayList<>();
    expectedAddedCategoryOilHistoryEntry.add("Added Category");
    expectedAddedCategoryOilHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "name", categoryOil.getName()));
    expectedAddedCategoryOilHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "parent category", categoryCar().getName()));

    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transaction, accountId, categoryCarId, token);

    List<String> expectedAddedTransactionHistoryEntry = new ArrayList<>();
    expectedAddedTransactionHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "name", transaction.getDescription()));
    expectedAddedTransactionHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "date", transaction.getDate()));
    expectedAddedTransactionHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "category", categoryCarId));
    for (AccountPriceEntry entry : transaction.getAccountPriceEntries()) {
      expectedAddedTransactionHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "price", entry.getPrice().toString()));
      expectedAddedTransactionHistoryEntry.add(String.format(ENTRY_VALUES_TEMPLATE, "account", entry.getAccountId()));
    }

    //when
    List<List<String>> listsOfEntries = callRestServiceToReturnHistoryEntry(token);

    //then
    assertThat(listsOfEntries, contains(expectedHistoryEntryAddAccount, expectedAddedCategoryCarHistoryEntry, expectedAddedCategoryOilHistoryEntry,
        expectedAddedTransactionHistoryEntry));
  }


  private List<List<String>> callRestServiceToReturnHistoryEntry(String token) throws Exception {
    String response =
        mockMvc
            .perform(get(HISTORY_PATH)
                .header(HttpHeaders.AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    List<HistoryEntry> historyEntriesFromResponse = getHistoryEntriesFromResponse(response);

    return historyEntriesFromResponse.stream()
        .map(historyEntry -> historyEntry.getEntry())
        .collect(Collectors.toList());
  }

  private List<HistoryEntry> getHistoryEntriesFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, HistoryEntry.class));
  }
}