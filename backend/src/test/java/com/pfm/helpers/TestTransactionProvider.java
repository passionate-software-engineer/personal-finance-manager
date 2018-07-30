package com.pfm.helpers;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionController.TransactionRequest;
import java.math.BigDecimal;

public class TestTransactionProvider {

//  public static final Transaction TRANSACTION_DESCRIPTION = Transaction.builder().id(Long.valueOf(1)).description("Cinema").category();

  public static final Long MOCK_TRANSACTION_ID = 1L;
  public static final String MOCK_TRANSACTION_DESCRIPTION = "Cinema";
  //  public static final String MOCK_UPDATED_TRANSACTION_DESCRIPTION = "Food";
  public static final Category MOCK_TRANSACTION_CATEGORY = new Category(1L, "entertainment", null);
  //  public static final Category MOCK_UPDATED_TRANSACTION_CATEGORY = new Category(1L,"groceries",null);
  public static final Account MOCK_TRANSACTION_ACCOUNT = new Account(1L, "ING BANK",
      BigDecimal.valueOf(1500));
  //  public static final Account MOCK_UPDATED_TRANSACTION_ACCOUNT = new Account(1L,"BGZ",BigDecimal.valueOf(2000));
  public static final BigDecimal MOCK_TRANSACTION_PRICE = BigDecimal.valueOf(150);
  //  public static final BigDecimal MOCK_UPDATED_TRANSACTION_PRICE=BigDecimal.valueOf(600);
  public static Transaction mockTransaction = new Transaction(MOCK_TRANSACTION_ID,
      MOCK_TRANSACTION_DESCRIPTION, MOCK_TRANSACTION_CATEGORY, MOCK_TRANSACTION_ACCOUNT,
      MOCK_TRANSACTION_PRICE);

  public static final Integer CATEGORY_ID=1;
  public static final Integer ACCOUNT_ID=1;
//  public static Transaction mockUpdatedTransaction= new Transaction(MOCK_TRANSACTION_ID,MOCK_UPDATED_TRANSACTION_DESCRIPTION,MOCK_UPDATED_TRANSACTION_CATEGORY,MOCK_UPDATED_TRANSACTION_ACCOUNT,MOCK_UPDATED_TRANSACTION_PRICE);

//  public static TransactionRequest mockTransaction = new TransactionRequest(MOCK_TRANSACTION_ID,
//      MOCK_TRANSACTION_DESCRIPTION, MOCK_TRANSACTION_CATEGORY, MOCK_TRANSACTION_ACCOUNT,
//      MOCK_TRANSACTION_PRICE);
//  CategoryRequest parentCategoryToAdd = CategoryRequest.builder().name("Car")

  public static final TransactionRequest transactionRequest = TransactionRequest.builder().description(MOCK_TRANSACTION_DESCRIPTION).categoryId(CATEGORY_ID).accountId(ACCOUNT_ID).price(MOCK_TRANSACTION_PRICE).build();
}
