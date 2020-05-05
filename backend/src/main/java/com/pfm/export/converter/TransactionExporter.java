package com.pfm.export.converter;

import static java.util.stream.Collectors.groupingBy;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportAccountPriceEntry;
import com.pfm.export.ExportResult.ExportTransaction;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.TransactionService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TransactionExporter {

  private final TransactionService transactionService;
  private final CategoryService categoryService;
  private final AccountService accountService;
  private final DateFormatter dateFormatter;

  public Map<String, List<ExportTransaction>> getTransactionsByMonth(long userId) {
    return findExportTransactions(userId)
        .stream()
        .collect(groupingBy(month()));
  }

  private Function<ExportTransaction, String> month() {
    return exportTransaction -> dateFormatter.toString(exportTransaction.getDate());
  }

  private List<ExportTransaction> findExportTransactions(long userId) {
    return transactionService.getTransactions(userId)
        .stream()
        .map(transaction -> mapToExportTransaction(userId, transaction))
        .collect(Collectors.toList());
  }

  private ExportTransaction mapToExportTransaction(long userId, final Transaction transaction) {
    return ExportTransaction.builder()
        .description(transaction.getDescription())
        .date(transaction.getDate())
        .accountPriceEntries(createAccountsPriceEntries(transaction.getAccountPriceEntries(), userId))
        .category(findCategoryName(userId, transaction.getCategoryId()))
        .build();
  }

  private String findCategoryName(long userId, long categoryId) {
    return categoryService.getCategoryByIdAndUserId(categoryId, userId).orElse(new Category()).getName();
  }

  private List<ExportAccountPriceEntry> createAccountsPriceEntries(final List<AccountPriceEntry> accountPriceEntries, long userId) {
    return accountPriceEntries.stream().map(
        accountPriceEntry -> ExportAccountPriceEntry.builder()
            .account(accountService.getAccountByIdAndUserId(accountPriceEntry.getAccountId(), userId).orElse(new Account()).getName())
            .price(accountPriceEntry.getPrice())
            .build()).collect(Collectors.toList());
  }

}
