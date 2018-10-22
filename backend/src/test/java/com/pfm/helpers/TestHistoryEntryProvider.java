package com.pfm.helpers;

import com.pfm.history.HistoryEntry;
import java.time.LocalDateTime;

public class TestHistoryEntryProvider {

  public static final HistoryEntry HISTORY_ENTRY_ACCOUNT_ADD =
      HistoryEntry.builder()
          .id(999L)
          .date(LocalDateTime.now())
          .entry("Account added by XXXX")
          .build();

  public static final HistoryEntry HISTORY_ENTRY_ACCOUNT_UPDATE =
      HistoryEntry.builder()
          .id(888L)
          .date(LocalDateTime.now())
          .entry("Account update by YYYY")
          .build();

  public static final HistoryEntry HISTORY_ENTRY_CATEGORY_ADD =
      HistoryEntry.builder()
          .id(666L)
          .date(LocalDateTime.now())
          .entry("Category added by WWWWW")
          .build();

  public static final HistoryEntry HISTORY_ENTRY_CATEGORY_DELETE =
      HistoryEntry.builder()
          .id(444L)
          .date(LocalDateTime.now())
          .entry("Category deleted by RRRR")
          .build();
}