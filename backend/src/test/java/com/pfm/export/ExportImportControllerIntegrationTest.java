package com.pfm.export;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.IMPORT_NOT_POSSIBLE;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestFilterProvider.filterFoodExpenses;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static java.math.RoundingMode.HALF_UP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.config.MessagesProvider;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterService;
import com.pfm.helpers.IntegrationTestsBase;
import com.pfm.helpers.TestAccountProvider;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExportImportControllerIntegrationTest extends IntegrationTestsBase {

  @Autowired
  private AccountService accountService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private FilterService filterService;

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldExportTransactions() throws Exception {
    // given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(2)); // PLN

    long jacekAccountId = callRestServiceToAddAccountAndReturnId(account, token);
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    callRestToAddCategoryAndReturnId(Category.builder()
        .name("Pizza")
        .parentCategory(Category.builder()
            .id(foodCategoryId)
            .build()
        ).build(), token);

    Transaction transactionToAddFood = foodTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transactionToAddFood, jacekAccountId, foodCategoryId, token);

    Filter filter = filterFoodExpenses();
    filter.getCategoryIds().add(foodCategoryId);
    filter.getAccountIds().add(jacekAccountId);
    callRestServiceToAddFilterAndReturnId(filter, token);
    // when
    // then
    mockMvc.perform(get(EXPORT_SERVICE_PATH)
        .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.sumOfAllFundsInBaseCurrency", is("1000.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap.EUR", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap.PLN", is("1000.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap.GBP", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap.USD", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.sumOfAllFundsInBaseCurrency", is("1010.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap.EUR", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap.PLN", is("1010.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap.GBP", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap.USD", is("0.00")))
        .andExpect(jsonPath("initialAccountsState", hasSize(1)))
        .andExpect(jsonPath("initialAccountsState[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("initialAccountsState[0].balance", is("1000.00")))
        .andExpect(jsonPath("finalAccountsState", hasSize(1)))
        .andExpect(jsonPath("finalAccountsState[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("finalAccountsState[0].balance", is("1010.00")))
        .andExpect(jsonPath("finalAccountsState[0].currency", is("PLN")))
        .andExpect(jsonPath("categories", hasSize(2)))
        .andExpect(jsonPath("categories[0].name", is(categoryFood().getName())))
        .andExpect(jsonPath("categories[0].parentCategoryName").doesNotExist())
        .andExpect(jsonPath("categories[1].name", is("Pizza")))
        .andExpect(jsonPath("categories[1].parentCategoryName", is(categoryFood().getName())))
        .andExpect(jsonPath("periods", hasSize(1)))
        .andExpect(jsonPath("periods[0].startDate", is("2018-08-01")))
        .andExpect(jsonPath("periods[0].endDate", is("2018-08-31")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheBeginningOfPeriod.currencyToFundsMap.EUR", is("0.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheBeginningOfPeriod.currencyToFundsMap.PLN", is("1000.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheBeginningOfPeriod.currencyToFundsMap.GBP", is("0.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheBeginningOfPeriod.currencyToFundsMap.USD", is("0.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheEndOfPeriod.currencyToFundsMap.EUR", is("0.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheEndOfPeriod.currencyToFundsMap.PLN", is("1010.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheEndOfPeriod.currencyToFundsMap.GBP", is("0.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheEndOfPeriod.currencyToFundsMap.USD", is("0.00")))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginningOfPeriod", hasSize(1)))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginningOfPeriod[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginningOfPeriod[0].balance", is("1000.00")))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginningOfPeriod[0].currency", is("PLN")))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod", hasSize(1)))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod[0].balance", is("1010.00")))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod[0].currency", is("PLN")))
        .andExpect(jsonPath("periods[0].transactions", hasSize(1)))
        .andExpect(jsonPath("periods[0].transactions[0].description", is(transactionToAddFood.getDescription())))
        .andExpect(jsonPath("periods[0].transactions[0].category", is(categoryFood().getName())))
        .andExpect(jsonPath("periods[0].transactions[0].date", is(transactionToAddFood.getDate().toString())))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries", hasSize(1)))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries[0].account", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries[0].price", is("10.00")))
        .andExpect(jsonPath("filters[0].name", is("Food")))
        .andExpect(jsonPath("filters[0].accounts", hasSize(1)))
        .andExpect(jsonPath("filters[0].accounts[0]", is("Jacek Millenium Bank savings")))
        .andExpect(jsonPath("filters[0].categories", hasSize(1)))
        .andExpect(jsonPath("filters[0].categories[0]", is("Food")))
        .andExpect(jsonPath("filters[0].priceFrom", is("100.00")))
        .andExpect(jsonPath("filters[0].priceTo", is("300.00")))
        .andExpect(jsonPath("filters[0].dateFrom", is("2018-03-01")))
        .andExpect(jsonPath("filters[0].dateTo", is("2018-03-31")))
        .andExpect(jsonPath("filters[0].description", is("Food expenses")))
    ;

    // TODO assert currency is exported
    // TODO assert map of currencies
  }

  @Test
  public void shouldExportTransactionsWhenNoDataIsAvailableInTheSystem() throws Exception {
    // given

    // when
    mockMvc.perform(get(EXPORT_SERVICE_PATH)
        .header("Authorization", token))

        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.sumOfAllFundsInBaseCurrency", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap", hasKey("EUR")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap", hasKey("PLN")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap", hasKey("GBP")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap", hasKey("USD")))
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport.currencyToFundsMap", hasValue("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.sumOfAllFundsInBaseCurrency", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap", hasKey("EUR")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap", hasKey("PLN")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap", hasKey("GBP")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap", hasKey("USD")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport.currencyToFundsMap", hasValue("0.00")))
        .andExpect(jsonPath("initialAccountsState", hasSize(0)))
        .andExpect(jsonPath("finalAccountsState", hasSize(0)))
        .andExpect(jsonPath("categories", hasSize(0)))
        .andExpect(jsonPath("periods", hasSize(0)))
        .andExpect(jsonPath("filters", hasSize(0)));

  }

  @Test
  public void shouldImportTransactions() throws Exception {
    // given
    ExportResult input = new ExportResult();
    input.setCategories(Arrays.asList(
        ExportCategory.builder()
            .name(categoryHome().getName())
            .build(),
        ExportCategory.builder()
            .name(categoryFood().getName())
            .parentCategoryName(categoryHome().getName())
            .build()
        )
    );

    ExportAccount aliorAccount = ExportAccount.builder()
        .name("Alior Bank")
        .balance(BigDecimal.TEN)
        .currency("USD")
        .build();

    ExportAccount ideaBankAccount = ExportAccount.builder()
        .name("Idea Bank")
        .balance(BigDecimal.ZERO)
        // should default to PLN
        .build();

    input.setInitialAccountsState(Arrays.asList(aliorAccount, ideaBankAccount));
    input.setFinalAccountsState(Arrays.asList(aliorAccount, ideaBankAccount));

    ExportAccountPriceEntry entry = ExportAccountPriceEntry.builder()
        .account(aliorAccount.getName()) // TODO add checkstyle check to detect magic strings & numbers
        .price(BigDecimal.valueOf(-124))
        .build();

    ExportTransaction transaction = ExportTransaction.builder()
        .category(categoryFood().getName())
        .date(LocalDate.now())
        .description("McDonalds")
        .accountPriceEntries(Collections.singletonList(entry))
        .build();

    ExportPeriod period = ExportPeriod.builder()
        .accountStateAtTheBeginningOfPeriod(Arrays.asList(aliorAccount, ideaBankAccount))
        .accountStateAtTheEndOfPeriod(Arrays.asList(aliorAccount, ideaBankAccount))
        .startDate(LocalDate.MIN)
        .endDate(LocalDate.MAX)
        .transactions(Collections.singletonList(transaction))
        .sumOfAllFundsAtTheBeginningOfPeriod(ExportFundsSummary.builder().sumOfAllFundsInBaseCurrency(BigDecimal.TEN).build())
        .sumOfAllFundsAtTheEndOfPeriod(ExportFundsSummary.builder().sumOfAllFundsInBaseCurrency(BigDecimal.TEN).build())
        .build();

    input.setPeriods(Collections.singletonList(period));

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isCreated());

    // then

    List<Account> accounts = accountService.getAccounts(userId);
    assertThat(accounts, hasSize(2));
    assertThat(accounts.get(0).getName(), is(aliorAccount.getName()));
    assertThat(accounts.get(0).getBalance(), is(aliorAccount.getBalance().add(entry.getPrice()).setScale(2, HALF_UP)));
    assertThat(accounts.get(1).getName(), is(ideaBankAccount.getName()));
    // TODO handle rounding in single place - create helper class and use everywhere, add method to format BigDecimal to string
    assertThat(accounts.get(1).getBalance(), is(ideaBankAccount.getBalance().setScale(2, HALF_UP)));

    List<Category> categories = categoryService.getCategories(userId);
    assertThat(categories, hasSize(2));
    assertThat(categories.get(0).getName(), is(input.getCategories().get(0).getName()));
    assertThat(categories.get(0).getParentCategory(), is(nullValue()));
    assertThat(categories.get(1).getName(), is(input.getCategories().get(1).getName()));
    assertThat(categories.get(1).getParentCategory().getName(), is(input.getCategories().get(1).getParentCategoryName()));

    List<Transaction> transactions = transactionService.getTransactions(userId);
    assertThat(transactions, hasSize(1));

    Transaction createdTransaction = transactions.get(0);
    assertThat(createdTransaction.getDate(), is(transaction.getDate()));
    assertThat(createdTransaction.getDescription(), is(transaction.getDescription()));
    assertThat(categoryService.getCategoryByIdAndUserId(createdTransaction.getCategoryId(), userId).orElseThrow(AssertionError::new).getName(),
        is(transaction.getCategory()));
    assertThat(createdTransaction.getAccountPriceEntries(), hasSize(1));
    assertThat(createdTransaction.getAccountPriceEntries().get(0).getPrice(), is(entry.getPrice().setScale(2, HALF_UP)));
    assertThat(
        accountService.getAccountByIdAndUserId(createdTransaction.getAccountPriceEntries().get(0).getAccountId(), userId)
            .orElseThrow(AssertionError::new).getName(),
        is(entry.getAccount()));
  }

  @Test
  public void shouldReturnErrorWhenDataIsImportedAgain() throws Exception {
    // given
    accountService.saveAccount(userId, TestAccountProvider.accountJacekBalance1000());

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(new ExportResult()))
        .contentType(JSON_CONTENT_TYPE))

        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", is(MessagesProvider.getMessage(IMPORT_NOT_POSSIBLE))));
  }

  @Test
  public void shouldReturnErrorWhenNotSupportedCurrencyWasProvided() throws Exception {
    // given
    ExportResult input = new ExportResult();
    input.setCategories(Arrays.asList(
        ExportCategory.builder()
            .name(categoryHome().getName())
            .build(),
        ExportCategory.builder()
            .name(categoryFood().getName())
            .parentCategoryName(categoryHome().getName())
            .build()
        )
    );

    ExportAccount japaneaseAccount = ExportAccount.builder()
        .name("Japanese Bank")
        .balance(BigDecimal.TEN)
        .currency("JPN")
        .build();

    input.setInitialAccountsState(Collections.singletonList(japaneaseAccount));

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE))

        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", is(String.format(getMessage(ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST), japaneaseAccount.getCurrency()))));
  }

  @Test
  public void shouldImportFilters() throws Exception {
    // given
    final ExportResult input = new ExportResult();

    final ExportResult.ExportFilter filter = new ExportResult.ExportFilter();

    final String filterName = "Pawel";
    filter.setName(filterName);
    final String filterDescription = "some description";
    filter.setDescription(filterDescription);
    final LocalDate filterFromDate = LocalDate.of(2000, 01, 01);
    filter.setDateFrom(filterFromDate);
    final LocalDate filterFromTo = LocalDate.of(2020, 01, 01);
    filter.setDateTo(filterFromTo);
    final BigDecimal filterPriceFrom = BigDecimal.valueOf(99L, 2);
    filter.setPriceFrom(filterPriceFrom);
    final BigDecimal filterPriceTo = BigDecimal.valueOf(19999L, 2);
    filter.setPriceTo(filterPriceTo);
    input.setCategories(Collections.singletonList(
        ExportCategory.builder()
            .name(categoryOil().getName())
            .build()
        )
    );
    filter.setCategories(Collections.singletonList(categoryOil().getName()));

    input.setInitialAccountsState(Collections.singletonList(ExportAccount.builder()
            .name(accountMbankBalance10().getName())
            .balance(accountMbankBalance10().getBalance())
            .currency(accountMbankBalance10().getCurrency().getName())
            .build()
        )
    );
    filter.setAccounts(Collections.singletonList(accountMbankBalance10().getName()));

    ExportResult.ExportFilter emptyFilter = new ExportResult.ExportFilter();
    emptyFilter.setName("All empty");

    input.setFilters(Arrays.asList(filter, emptyFilter));

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isCreated());

    // then

    List<Filter> filters
        = filterService.getAllFilters(userId);
    assertThat(filters, hasSize(2));
    assertThat(filters.get(0).getName(), is(filterName));
    assertThat(filters.get(0).getDateFrom(), is(filterFromDate));
    assertThat(filters.get(0).getDateTo(), is(filterFromTo));
    assertThat(filters.get(0).getDescription(), is(filterDescription));
    assertThat(filters.get(0).getPriceFrom(), is(filterPriceFrom));
    assertThat(filters.get(0).getPriceTo(), is(filterPriceTo));
    assertThat(filters.get(0).getCategoryIds(), hasSize(1));
    assertThat(filters.get(0).getCategoryIds().get(0), is(1L));
    assertThat(filters.get(0).getAccountIds(), hasSize(1));
    assertThat(filters.get(0).getAccountIds().get(0), is(6L));

    // TODO empty filter assertions

  }

  @Test
  public void sendFilterWithoutName() throws Exception {

    // given
    ExportResult input = new ExportResult();
    ExportResult.ExportFilter filterEmptyString = ExportResult.ExportFilter.builder().name("").build();
    ExportResult.ExportFilter filterNullObject = ExportResult.ExportFilter.builder().name(null).build();
    ExportResult.ExportFilter filterWithWhiteblanks = ExportResult.ExportFilter.builder().name("   ").build();
    input.setFilters(Arrays.asList(filterEmptyString, filterNullObject, filterWithWhiteblanks));

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE))

        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", is("Filter is missing name")))
        .andExpect(jsonPath("$[1]", is("Filter is missing name")))
        .andExpect(jsonPath("$[2]", is("Filter is missing name")));

  }

}
