package com.pfm.transaction.import1;

import static com.pfm.export.ImportService.CATEGORY_NAMED_IMPORTED;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.category.CategoryRepository;
import com.pfm.category.CategoryService;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import com.pfm.transaction.import1.csv.ing.ParsedFromIngCsv;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CsvImportService {

  private final CategoryService categoryService;
  private final CategoryRepository categoryRepository;
  private final CsvParser csvParser;

  public Collection<Transaction> importTransactions(File file, long userId, long targetAccountId, List<Account> userAccounts)
      throws TransactionsParsingException {
    List<Category> importedCategoryList = categoryRepository.findByNameIgnoreCaseAndUserId(CATEGORY_NAMED_IMPORTED, userId);
    long createdImportCategoryId = 0L;
    boolean importCategoryExist = !importedCategoryList.isEmpty();
    if (!importCategoryExist) {
      createdImportCategoryId = categoryService.addImportedCategory(userId).getId();
    }
    long importCategoryId = importCategoryExist ? importedCategoryList.get(0).getId() : createdImportCategoryId;
    List<ParsedFromIngCsv> parsedBeans = parse(file);
    return convertParsedBeansToTransactions(parsedBeans, targetAccountId, importCategoryId, userAccounts);
  }

  public List<Transaction> convertParsedBeansToTransactions(List<ParsedFromIngCsv> parsedBeans, long targetAccountId, long importedCategoryId,
      List<Account> userAccounts) {
    List<Transaction> transactionsFromBeans = new ArrayList<>();
    for (ParsedFromIngCsv parsedBean : parsedBeans) {
      transactionsFromBeans.add(convertParsedBeanToTransaction(parsedBean, targetAccountId, userAccounts, importedCategoryId));
    }
    return transactionsFromBeans;
  }

  private Transaction convertParsedBeanToTransaction(ParsedFromIngCsv parsedBean, long targetAccountId, List<Account> userAccounts,
      long importedCategoryId) {
    List<AccountPriceEntry> entries = new ArrayList<>();

    AccountPriceEntry entry = AccountPriceEntry.builder()
        .accountId(targetAccountId)
        .price(parsedBean.getTransactionAmount())
        .build();

    entries.add(entry);

    final Optional<Long> accountIdForOwnTransferOptional = getAccountIdForOwnTransfer(parsedBean, userAccounts);
    if (accountIdForOwnTransferOptional.isPresent()) {
      AccountPriceEntry ownTransferEntry = AccountPriceEntry.builder()
          .accountId(accountIdForOwnTransferOptional.get())
          .price(parsedBean.getTransactionAmount().negate())
          .build();

      entries.add(ownTransferEntry);
    }

    return Transaction.builder()
        .importId(parsedBean.getImportId())
        .categoryId(importedCategoryId)
        .accountPriceEntries(entries)
        .description(parsedBean.getTransactionDescriptionCandidates()
            .collect(Collectors.joining("#")))
        .date(parsedBean.getTransactionDate())
        .build();
  }

  private Optional<Long> getAccountIdForOwnTransfer(ParsedFromIngCsv parsedBean, List<Account> userAccounts) {
    return userAccounts.stream()
        .filter(account -> account.getBankAccountNumber().equals(parsedBean.getReceiverBankAccountNumber()))
        .findAny()
        .map(Account::getId);
  }

  private List<ParsedFromIngCsv> parse(File file) throws TransactionsParsingException {

    return csvParser.parseBeans(file);
  }

}
