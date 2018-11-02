package com.pfm.history;

import static com.pfm.helpers.TestHistoryEntryProvider.HISTORY_ENTRY_ACCOUNT_ADD;
import static com.pfm.helpers.TestHistoryEntryProvider.HISTORY_ENTRY_ACCOUNT_UPDATE;
import static com.pfm.helpers.TestHistoryEntryProvider.HISTORY_ENTRY_CATEGORY_ADD;
import static com.pfm.helpers.TestHistoryEntryProvider.HISTORY_ENTRY_CATEGORY_DELETE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

  @InjectMocks
  private HistoryEntryService historyEntryService;

  @Test
  public void shouldGetAllAHistory() {
    //given
    long userId = 12412L;

    when(historyEntryRepository.findByUserId(userId)).thenReturn(
        Arrays.asList(HISTORY_ENTRY_ACCOUNT_ADD,
            HISTORY_ENTRY_ACCOUNT_UPDATE,
            HISTORY_ENTRY_CATEGORY_ADD,
            HISTORY_ENTRY_CATEGORY_DELETE));

    //when
    List<HistoryEntry> actualHistoryEntryList = historyEntryService.getHistoryEntries(userId);

    //then
    ArrayList<HistoryEntry> list = new ArrayList<>();
    list.add(HISTORY_ENTRY_CATEGORY_DELETE);
    list.add(HISTORY_ENTRY_CATEGORY_ADD);
    list.add(HISTORY_ENTRY_ACCOUNT_UPDATE);
    list.add(HISTORY_ENTRY_ACCOUNT_ADD);
    int i = 0;

    for (HistoryEntry historyEntryChecker : list) {
      HistoryEntry historyEntry = actualHistoryEntryList.get(i++);
      assertThat(historyEntry.getId(), is(equalTo(historyEntryChecker.getId())));
      assertThat(historyEntry.getEntry(), is(equalTo(historyEntryChecker.getEntry())));
      assertThat(historyEntry.getDate(), is(equalTo(historyEntryChecker.getDate())));
    }
  }
}