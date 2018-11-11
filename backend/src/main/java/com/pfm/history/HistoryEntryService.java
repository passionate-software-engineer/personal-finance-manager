package com.pfm.history;

import com.pfm.history.HistoryEntry.Type;
import java.time.LocalDateTime;
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
  private HistoryEntryProvider historyEntryProvider;

  public List<HistoryEntry> getHistoryEntries(long userId) {
    return historyEntryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(HistoryEntry::getId))
        .collect(Collectors.toList());
  }

  public void addHistoryEntryOnAdd(Object object, long userId) {
    List<HistoryInfo> historyEntryOnAdd = historyEntryProvider.createHistoryEntryOnAdd(object);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(LocalDateTime.now())
        .type(Type.ADD)
        .entries(historyEntryOnAdd)
        .object(object.getClass().getSimpleName())
        .userId(userId)
        .build();
    saveHistoryEntry(historyEntry);
  }

  public void addHistoryEntryOnUpdate(Object oldObject, Object newObject, long userId) {
    List<HistoryInfo> historyEntryOnAdd = historyEntryProvider.createHistoryEntryOnUpdate(oldObject, newObject);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(LocalDateTime.now())
        .type(Type.UPDATE)
        .entries(historyEntryOnAdd)
        .object(oldObject.getClass().getSimpleName())
        .userId(userId)
        .build();
    saveHistoryEntry(historyEntry);
  }

  // TODO refactor security to not force develeopers to pass userId all around.
  public void addHistoryEntryOnDelete(Object oldObject, long userId) {
    List<HistoryInfo> historyEntryOnAdd = historyEntryProvider.createHistoryEntryOnDelete(oldObject);
    HistoryEntry historyEntry = HistoryEntry.builder()
        .date(LocalDateTime.now())
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

