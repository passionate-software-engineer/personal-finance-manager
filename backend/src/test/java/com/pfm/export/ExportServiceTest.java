package com.pfm.export;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.converter.AccountExporter;
import com.pfm.export.converter.CategoryExporter;
import com.pfm.export.converter.FilterExporter;
import com.pfm.export.converter.FundsExporter;
import com.pfm.export.converter.PeriodExporter;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

  private static final long USER_ID = 1L;
  @Mock
  private PeriodExporter periodExporter;
  @Mock
  private CategoryExporter categoryExporter;
  @Mock
  private FilterExporter filterExporter;
  @Mock
  private AccountExporter accountExporter;
  @Mock
  private FundsExporter fundsExporter;
  @Mock
  private HistoryEntryService historyEntryService;
  @InjectMocks
  private ExportService exportService;

  @Test
  public void shouldExportReportWithDataFromExporters() {
    // given
    List<ExportAccount> exportAccounts = List.of(new ExportAccount());

    when(periodExporter.export(USER_ID)).thenReturn(List.of());
    when(categoryExporter.export(USER_ID)).thenReturn(List.of(new ExportCategory()));
    when(filterExporter.export(USER_ID)).thenReturn(List.of(new ExportFilter()));
    when(accountExporter.export(USER_ID)).thenReturn(exportAccounts);
    when(fundsExporter.export(exportAccounts, USER_ID)).thenReturn(new ExportFundsSummary());
    when(historyEntryService.prepareExportHistory(anyList())).thenReturn(List.of());

    // when
    ExportResult exportResult = exportService.exportData(USER_ID);

    // then
    Assertions.assertEquals(exportResult.getPeriods().size(), 0);
    Assertions.assertEquals(exportResult.getCategories().size(), 1);
    Assertions.assertEquals(exportResult.getFilters().size(), 1);
    Assertions.assertEquals(exportResult.getFinalAccountsState().size(), 1);
    Assertions.assertNotNull(exportResult.getSumOfAllFundsAtTheBeginningOfExport());
    Assertions.assertNotNull(exportResult.getSumOfAllFundsAtTheEndOfExport());

  }
}
