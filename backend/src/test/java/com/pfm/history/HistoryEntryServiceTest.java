package com.pfm.history;

import static com.pfm.helpers.TestAccountProvider.accountMbankBalance10;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestHelper.convertDoubleToBigDecimal;
import static com.pfm.helpers.TestTransactionProvider.carTransactionWithNoAccountAndNoCategory;
import static com.pfm.helpers.TestTransactionProvider.foodTransactionWithNoAccountAndNoCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.history.HistoryEntry.Type;
import com.pfm.transaction.AccountPriceEntry;
import com.pfm.transaction.Transaction;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HistoryEntryServiceTest {

  @Mock
  private HistoryEntryRepository historyEntryRepository;

  @Mock
  private HistoryInfoProvider historyInfoProvider;

  @InjectMocks
  private HistoryEntryService historyEntryService;

  @Test
  public void shouldThrowExceptionCausedByTwoArgumentsOfDifferentTypes() {
    //given
    Account account = new Account();
    Category category = new Category();

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      historyEntryService.addHistoryEntryOnUpdate(category, account, 1L);
    });

    // then
    assertThat(exception.getMessage(), is("Parameters oldObject and newObject are not the same types"));
  }

  @Test
  public void shouldFilterOutIdsFromHistoryEntries() {
    //given
    ZonedDateTime date = ZonedDateTime.now();
    Transaction transaction = getTransaction();

    List<HistoryInfo> historyInfos = new ArrayList<>();
    historyInfos.add(HistoryInfo.builder()
        .id(2L)
        .name("description")
        .newValue(transaction.getDescription())
        .build());
    historyInfos.add(HistoryInfo.builder()
        .id(3L)
        .name("categoryId")
        .newValue(categoryCar().getName())
        .build());
    historyInfos.add(HistoryInfo.builder()
        .id(4L)
        .name("date")
        .newValue(transaction.getDate().toString())
        .build());
    historyInfos.add(HistoryInfo.builder()
        .id(5L)
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    List<HistoryEntry> historyEntries = new ArrayList<>();
    historyEntries.add(HistoryEntry.builder()
        .id(1L)
        .date(date)
        .type(Type.ADD)
        .object("Account")
        .entries(historyInfos)
        .userId(1L)
        .build()
    );

    List<HistoryInfo> expectedHistoryInfos = new ArrayList<>();
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("description")
        .newValue(transaction.getDescription())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("categoryId")
        .newValue(categoryCar().getName())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("date")
        .newValue(transaction.getDate().toString())
        .build());
    expectedHistoryInfos.add(HistoryInfo.builder()
        .name("accountPriceEntries")
        .newValue(String.format("[%s : %s]", accountMbankBalance10().getName(), transaction.getAccountPriceEntries().get(0).getPrice()))
        .build());

    List<HistoryEntry> expectedHistoryEntries = new ArrayList<>();
    expectedHistoryEntries.add(HistoryEntry.builder()
        .date(date)
        .type(Type.ADD)
        .object("Account")
        .entries(expectedHistoryInfos)
        .build()
    );

    //when
    List<HistoryEntry> resultWithoutIds = historyEntryService.prepareExportHistory(historyEntries);

    //then
    assertThat(resultWithoutIds, is(expectedHistoryEntries));

  }

  private Transaction getTransaction() {
    Transaction transaction = foodTransactionWithNoAccountAndNoCategory();
    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(100.00))
        .accountId(1L)
        .build()
    ));
    transaction.setCategoryId(1L);
    return transaction;
  }

  //fixme - extract to class like transactionProvider - same method is in HistoryInfoProviderTest
  private Transaction getTransactionWithNewValues() {
    Transaction transaction = carTransactionWithNoAccountAndNoCategory();
    transaction.setDescription("Food for party");
    transaction.setDate(LocalDate.of(2018, 10, 10));

    transaction.setAccountPriceEntries(Collections.singletonList(AccountPriceEntry.builder()
        .price(convertDoubleToBigDecimal(321.00))
        .accountId(2L)
        .build()
    ));
    transaction.setCategoryId(2L);
    return transaction;
  }
}
