package com.pfm.export;

import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportCategory;
import com.pfm.export.ExportResult.ExportFilter;
import com.pfm.export.ExportResult.ExportFundsSummary;
import com.pfm.export.ExportResult.ExportPeriod;
import com.pfm.export.converter.AccountExporter;
import com.pfm.export.converter.CategoryExporter;
import com.pfm.export.converter.FilterExporter;
import com.pfm.export.converter.FundsExporter;
import com.pfm.export.converter.PeriodExporter;
import com.pfm.history.HistoryEntry;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportService {

  private final PeriodExporter periodExporter;
  private final CategoryExporter categoryExporter;
  private final FilterExporter filterExporter;
  private final AccountExporter accountExporter;
  private final FundsExporter fundsExporter;
  private final HistoryEntryService historyEntryService;

  ExportResult exportData(long userId) {
    List<ExportPeriod> periods = periodExporter.export(userId);
    List<ExportAccount> accounts = accountExporter.export(userId);
    List<ExportAccount> accountStateAtTheBeginningOfPeriod = periods.isEmpty() ? accounts : lastPeriod(periods);

    List<ExportFilter> filters = filterExporter.export(userId);
    List<ExportCategory> categories = categoryExporter.export(userId);
    ExportFundsSummary sumOfAllFundsAtTheEndOfExport = fundsExporter.export(accounts, userId);
    ExportFundsSummary sumOfAllFundsAtTheBeginningOfExport = fundsExporter.export(accountStateAtTheBeginningOfPeriod, userId);

    // TODO: History entry should be mapped to DTO and as you can see - we use here service.method(service.method()) - looks a bit odd
    List<HistoryEntry> historyEntries = historyEntryService.prepareExportHistory(historyEntryService.getHistoryEntries(userId));

    return ExportResult.builder()
        .filters(filters)
        .categories(categories)
        .finalAccountsState(accounts)
        .sumOfAllFundsAtTheEndOfExport(sumOfAllFundsAtTheEndOfExport)
        .periods(periods)
        .initialAccountsState(accountStateAtTheBeginningOfPeriod)
        .sumOfAllFundsAtTheBeginningOfExport(sumOfAllFundsAtTheBeginningOfExport)
        .historyEntries(historyEntries)
        .build();
  }

  private List<ExportAccount> lastPeriod(final List<ExportPeriod> periods) {
    return periods.get(periods.size() - 1).getAccountStateAtTheBeginningOfPeriod();
  }
}
