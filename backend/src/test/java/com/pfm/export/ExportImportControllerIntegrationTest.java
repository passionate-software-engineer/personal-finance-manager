package com.pfm.export;

import static com.pfm.test.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.test.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.test.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.test.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());
    long pizzaCategoryId = callRestToAddCategoryAndReturnId(Category.builder()
        .name("Pizza")
        .parentCategory(Category.builder()
            .id(foodCategoryId)
            .build()
        )
        .build()
    );

    Transaction transactionToAddFood = foodTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(transactionToAddFood, jacekAccountId, foodCategoryId);

    Transaction transactionToAddPizza = foodTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(transactionToAddPizza, transactionId, jacekAccountId, pizzaCategoryId);

    //when
    mockMvc.perform(get(EXPORT_SERVICE_PATH))
        .andExpect(status().isOk());

    //then

    // TODO add assertions
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
