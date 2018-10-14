package com.pfm.export;

import static com.pfm.test.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.test.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.test.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.test.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.category.Category;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.test.helpers.IntegrationTestsBase;
import com.pfm.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class ExportImportControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldExportTransactions() throws Exception {
    // given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());
    callRestToAddCategoryAndReturnId(Category.builder()
        .name("Pizza")
        .parentCategory(Category.builder()
            .id(foodCategoryId)
            .build()
        )
        .build()
    );

    Transaction transactionToAddFood = foodTransactionWithNoAccountAndNoCategory();
    callRestToAddTransactionAndReturnId(transactionToAddFood, jacekAccountId, foodCategoryId);

    // when
    // then
    mockMvc.perform(get(EXPORT_SERVICE_PATH))
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
        .andExpect(jsonPath("periods[0].transactions[0].accountPriceEntries[0].price", is("10.00")))
        .andDo(print());
  }

  @Test
  public void shouldImportTransactions() throws Exception {
    //given
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
        .build();

    ExportAccount ideaBankAccount = ExportAccount.builder()
        .name("Idea Bank")
        .balance(BigDecimal.ZERO)
        .build();

    input.setInitialAccountsState(Arrays.asList(aliorAccount, ideaBankAccount));
    input.setFinalAccountsState(Arrays.asList(aliorAccount, ideaBankAccount));

    ExportAccountPriceEntry entry = ExportAccountPriceEntry.builder()
        .account(aliorAccount.getName()) // TODO add check to detect magic strings & numbers
        .price(BigDecimal.valueOf(124))
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

    //when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE)
    )
        .andExpect(status().isOk());

    //then

    // TODO add assertions
  }

}
