package com.pfm.export;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static java.math.RoundingMode.HALF_UP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
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

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldExportTransactions() throws Exception {
    // given
    Account account = accountJacekBalance1000();
    account.setCurrency(currencyService.getCurrencies(userId).get(0));

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

    // when
    // then
    mockMvc.perform(get(EXPORT_SERVICE_PATH)
        .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport", is("1000.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport", is("1010.00")))
        .andExpect(jsonPath("initialAccountsState", hasSize(1)))
        .andExpect(jsonPath("initialAccountsState[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("initialAccountsState[0].balance", is("1000.00")))
        .andExpect(jsonPath("finalAccountsState", hasSize(1)))
        .andExpect(jsonPath("finalAccountsState[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("finalAccountsState[0].balance", is("1010.00")))
        .andExpect(jsonPath("categories", hasSize(2)))
        .andExpect(jsonPath("categories[0].name", is(categoryFood().getName())))
        .andExpect(jsonPath("categories[0].parentCategoryName").doesNotExist())
        .andExpect(jsonPath("categories[1].name", is("Pizza")))
        .andExpect(jsonPath("categories[1].parentCategoryName", is(categoryFood().getName())))
        .andExpect(jsonPath("periods", hasSize(1)))
        .andExpect(jsonPath("periods[0].startDate", is("2018-08-01")))
        .andExpect(jsonPath("periods[0].endDate", is("2018-08-31")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheBeginningOfPeriod", is("1000.00")))
        .andExpect(jsonPath("periods[0].sumOfAllFundsAtTheEndOfPeriod", is("1010.00")))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginingOfPeriod", hasSize(1)))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginingOfPeriod[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].accountStateAtTheBeginingOfPeriod[0].balance", is("1000.00")))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod", hasSize(1)))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod[0].name", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].accountStateAtTheEndOfPeriod[0].balance", is("1010.00")))
        .andExpect(jsonPath("periods[0].transactions", hasSize(1)))
        .andExpect(jsonPath("periods[0].transactions[0].description", is(transactionToAddFood.getDescription())))
        .andExpect(jsonPath("periods[0].transactions[0].category", is(categoryFood().getName())))
        .andExpect(jsonPath("periods[0].transactions[0].date", is(transactionToAddFood.getDate().toString())))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries", hasSize(1)))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries[0].account", is(accountJacekBalance1000().getName())))
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries[0].price", is("10.00")));

    // TODO assert currency is exported
  }

  @Test
  public void shouldExportTransactionsWhenNoDataIsAvailableInTheSystem() throws Exception {
    // given

    // when
    mockMvc.perform(get(EXPORT_SERVICE_PATH)
        .header("Authorization", token))

        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("sumOfAllFundsAtTheBeginningOfExport", is("0.00")))
        .andExpect(jsonPath("sumOfAllFundsAtTheEndOfExport", is("0.00")))
        .andExpect(jsonPath("initialAccountsState", hasSize(0)))
        .andExpect(jsonPath("finalAccountsState", hasSize(0)))
        .andExpect(jsonPath("categories", hasSize(0)))
        .andExpect(jsonPath("periods", hasSize(0)));
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
        .currency("PLN")
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
        .accountStateAtTheBeginingOfPeriod(Arrays.asList(aliorAccount, ideaBankAccount))
        .accountStateAtTheEndOfPeriod(Arrays.asList(aliorAccount, ideaBankAccount))
        .startDate(LocalDate.MIN)
        .endDate(LocalDate.MAX)
        .transactions(Collections.singletonList(transaction))
        .sumOfAllFundsAtTheBeginningOfPeriod(BigDecimal.TEN)
        .sumOfAllFundsAtTheEndOfPeriod(BigDecimal.TEN)
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
    accountService.addAccount(userId, TestAccountProvider.accountJacekBalance1000());

    // when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .header("Authorization", token)
        .content(json(new ExportResult()))
        .contentType(JSON_CONTENT_TYPE))

        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", is(MessagesProvider.getMessage(MessagesProvider.IMPORT_NOT_POSSIBLE))));
  }

}
