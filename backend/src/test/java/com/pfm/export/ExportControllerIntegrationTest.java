package com.pfm.export;

import static com.pfm.helpers.TestAccountProvider.accountJacekBalance1000;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import com.pfm.transaction.Transaction;
import org.junit.Test;

public class ExportControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldExportTransactions() throws Exception {
    //given
    long jacekAccountId = callRestServiceToAddAccountAndReturnId(accountJacekBalance1000());
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood());

    Transaction transactionToAdd = foodTransactionWithNoAccountAndNoCategory();
    long transactionId = callRestToAddTransactionAndReturnId(transactionToAdd, jacekAccountId, foodCategoryId);

    Transaction addedTransaction = foodTransactionWithNoAccountAndNoCategory();
    setTransactionIdAccountIdCategoryId(addedTransaction, transactionId, jacekAccountId, foodCategoryId);

    //when
    mockMvc.perform(get(EXPORT_SERVICE_PATH))
        .andExpect(status().isOk());

    //then

  }

  @Test
  public void shouldImportTransactions() throws Exception {
    //given
    ExportResult input = new ExportResult();

    //when
    mockMvc.perform(post(IMPORT_SERVICE_PATH)
        .content(json(input))
        .contentType(JSON_CONTENT_TYPE)
    )
        .andExpect(status().isOk());

    //then

  }

}
