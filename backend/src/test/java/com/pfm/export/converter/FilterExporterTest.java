package com.pfm.export.converter;

import static org.mockito.Mockito.when;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.filter.Filter;
import com.pfm.filter.FilterService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilterExporterTest {

  private static final long USER_ID = 1L;

  private static final long ACCOUNT_ID = 2L;

  private static final long CATEGORY_ID = 2L;

  @Mock
  private FilterService filterService;

  @Mock
  private AccountService accountService;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private FilterExporter filterExporter;

  @Test
  public void shouldConvertFilterToFilterExport() {
    // given
    Filter filter = createFilter();
    Category category = createCategory();
    Account account = createAccount();

    when(filterService.getAllFilters(USER_ID)).thenReturn(List.of(filter));
    when(categoryService.getCategoryByIdAndUserId(CATEGORY_ID, USER_ID)).thenReturn(Optional.of(category));
    when(accountService.getAccountByIdAndUserId(ACCOUNT_ID, USER_ID)).thenReturn(Optional.of(account));

    // when
    List<ExportFilter> export = filterExporter.export(USER_ID);

    // then
    Assertions.assertEquals(export.size(), 1);
    ExportFilter exportFilter = export.get(0);
    Assertions.assertEquals(exportFilter.getAccounts(), List.of(account.getName()));
    Assertions.assertEquals(exportFilter.getCategories(), List.of(category.getName()));
    Assertions.assertEquals(exportFilter.getDateFrom(), filter.getDateFrom());
    Assertions.assertEquals(exportFilter.getDateTo(), filter.getDateTo());
    Assertions.assertEquals(exportFilter.getDescription(), filter.getDescription());
    Assertions.assertEquals(exportFilter.getName(), filter.getName());
    Assertions.assertEquals(exportFilter.getPriceFrom(), filter.getPriceFrom());
    Assertions.assertEquals(exportFilter.getPriceTo(), filter.getPriceTo());

  }

  private Category createCategory() {
    return Category.builder()
        .name("CATEGORY_NAME")
        .build();
  }

  private Account createAccount() {
    return Account.builder()
        .name("ACCOUNT_NAME")
        .build();
  }

  private Filter createFilter() {
    return Filter.builder()
        .accountIds(List.of(ACCOUNT_ID))
        .categoryIds(List.of(CATEGORY_ID))
        .dateFrom(LocalDate.MIN)
        .dateTo(LocalDate.MAX)
        .description("DESCRIPTION")
        .priceFrom(BigDecimal.ZERO)
        .priceTo(BigDecimal.TEN)
        .name("NAME")
        .build();
  }
}
