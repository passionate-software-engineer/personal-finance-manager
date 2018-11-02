package com.pfm.helpers;

import com.pfm.history.HistoryEntry;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class TestHistoryEntryProvider {

  public static final HistoryEntry HISTORY_ENTRY_ACCOUNT_ADD =
      HistoryEntry.builder()
          .id(999L)
          .date(LocalDateTime.now())
          .entry(Collections.singletonList("Account added by XXXX"))
          .build();

  public static final HistoryEntry HISTORY_ENTRY_ACCOUNT_UPDATE =
      HistoryEntry.builder()
          .id(888L)
          .date(LocalDateTime.now())
          .entry(Collections.singletonList("Account update by YYYY"))
          .build();

  public static final HistoryEntry HISTORY_ENTRY_CATEGORY_ADD =
      HistoryEntry.builder()
          .id(666L)
          .date(LocalDateTime.now())
          .entry(Collections.singletonList("Category added by WWWWW"))
          .build();

  public static final HistoryEntry HISTORY_ENTRY_CATEGORY_DELETE =
      HistoryEntry.builder()
          .id(444L)
          .date(LocalDateTime.now())
          .entry(Collections.singletonList("Category deleted by RRRR"))
          .build();
}