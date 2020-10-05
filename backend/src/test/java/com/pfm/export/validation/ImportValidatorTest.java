package com.pfm.export.validation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfm.export.ExportResult;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.history.HistoryEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportValidatorTest {

  @Mock
  private ImportCategoryValidator categories;
  @Mock
  private ImportFiltersValidator filters;
  @Mock
  private ImportHistoryEntriesValidator historyEntries;
  @Mock
  private ImportAccountsStateValidator accountsState;
  @Mock
  private ImportPeriodsValidator periods;
  @Mock
  private ImportSumOfAllFundsValidator sumOfAllFunds;
  @InjectMocks
  private ImportValidator importValidator;

  private ExportResult exportResult;

  @BeforeEach
  void setUp() {
    final List<ExportCategory> exportCategories = new ArrayList<>();
    final List<ExportFilter> exportFilters = new ArrayList<>();
    final List<ExportAccount> exportAccounts = new ArrayList<>();
    final List<HistoryEntry> historyEntries = new ArrayList<>();
    final List<ExportPeriod> exportPeriods = new ArrayList<>();

    exportResult = new ExportResult();
    exportResult.setCategories(exportCategories);
    exportResult.setFilters(exportFilters);
    exportResult.setInitialAccountsState(exportAccounts);
    exportResult.setFinalAccountsState(exportAccounts);
    exportResult.setHistoryEntries(historyEntries);
    exportResult.setPeriods(exportPeriods);
  }

  @Test
  void shouldCorrectlyReturnReceivedResult() {
    when(categories.validate(exportResult.getCategories())).thenReturn(Collections.singletonList("category"));
    when(filters.validate(exportResult.getFilters())).thenReturn(Collections.singletonList("filters"));
    when(accountsState.validate(exportResult.getInitialAccountsState(), "initial ")).thenReturn(Collections.singletonList("initialAccount"));
    when(accountsState.validate(exportResult.getFinalAccountsState(), "final ")).thenReturn(Collections.singletonList("finalAccount"));
    when(historyEntries.validate(exportResult.getHistoryEntries())).thenReturn(Collections.singletonList("history"));
    when(periods.validate(exportResult.getPeriods())).thenReturn(Collections.singletonList("periods"));
    when(sumOfAllFunds.validate(exportResult)).thenReturn(Collections.singletonList("sum"));

    final List<String> validationResult = importValidator.validate(exportResult);

    verify(categories).validate(exportResult.getCategories());
    verify(filters).validate(exportResult.getFilters());
    verify(accountsState).validate(exportResult.getInitialAccountsState(), "initial ");
    verify(accountsState).validate(exportResult.getFinalAccountsState(), "final ");
    verify(historyEntries).validate(exportResult.getHistoryEntries());
    verify(periods).validate(exportResult.getPeriods());
    verify(sumOfAllFunds).validate(exportResult);
    Assertions.assertEquals("category", validationResult.get(0));
    Assertions.assertEquals("filters", validationResult.get(1));
    Assertions.assertEquals("initialAccount", validationResult.get(2));
    Assertions.assertEquals("finalAccount", validationResult.get(3));
    Assertions.assertEquals("history", validationResult.get(4));
    Assertions.assertEquals("periods", validationResult.get(5));
    Assertions.assertEquals("sum", validationResult.get(6));
  }
}
