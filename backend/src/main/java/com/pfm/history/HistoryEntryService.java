package com.pfm.history;

import com.pfm.history.HistoryEntry.Type;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class HistoryEntryService {

  private HistoryEntryRepository historyEntryRepository;
  private HistoryInfoProvider historyInfoProvider;

  public List<HistoryEntry> getHistoryEntries(long userId) {
    return historyEntryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(HistoryEntry::getId))
        .collect(Collectors.toList());
  }

  public void addHistoryEntryOnAdd(Object object, long userId) {
    List<HistoryInfo> historyInfos = historyInfoProvider.createHistoryInfosOnAdd(object, userId);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(ZonedDateTime.now())
        .type(Type.ADD)
        .entries(historyInfos)
        .object(object.getClass().getSimpleName())
        .userId(userId)
        .build();
    saveHistoryEntry(historyEntry);
  }

  public <T> void addHistoryEntryOnUpdate(T oldObject, T newObject, long userId) {

    if (oldObject.getClass() != newObject.getClass()) {
      throw new IllegalStateException("Parameters oldObject and newObject are not the same types");
    }

    List<HistoryInfo> historyEntryOnAdd = historyInfoProvider.createHistoryEntryOnUpdate(oldObject, newObject, userId);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(ZonedDateTime.now())
        .type(Type.UPDATE)
        .entries(historyEntryOnAdd)
        .object(oldObject.getClass().getSimpleName())
        .userId(userId)
        .build();
    saveHistoryEntry(historyEntry);
  }

  // TODO refactor security to not force developers to pass userId all around.
  public void addHistoryEntryOnDelete(Object oldObject, long userId) {
    List<HistoryInfo> historyEntryOnAdd = historyInfoProvider.createHistoryEntryOnDelete(oldObject, userId);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(ZonedDateTime.now())
        .type(Type.DELETE)
        .entries(historyEntryOnAdd)
        .object(oldObject.getClass().getSimpleName())
        .userId(userId)
        .build();
    saveHistoryEntry(historyEntry);
  }

  private void saveHistoryEntry(HistoryEntry historyEntry) {
    historyEntryRepository.save(historyEntry);
  }

}

