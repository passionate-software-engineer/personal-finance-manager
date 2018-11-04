package com.pfm.history;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

  private static final String ADD_ENTRY_TEMPLATE = "Added %s";
  private static final String DELETE_ENTRY_TEMPLATE = "Deleted %s '%s'";
  private static final String CHANGES_TEMPLATE = "%s '%s' changes";

  private HistoryEntryRepository historyEntryRepository;

  public List<HistoryEntry> getHistoryEntries(long userId) {
    return historyEntryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(HistoryEntry::getId))
        .collect(Collectors.toList());
  }

  // TODO refactor security to not force develeopers to pass userId all around.
  public <T extends DifferenceProvider<T>> void addEntryOnAdd(T newObject, long userId) {
    List<String> entries = newObject.getObjectPropertiesWithValues();
    entries.add(0, String.format(ADD_ENTRY_TEMPLATE, newObject.getClass().getSimpleName()));
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnDelete(T oldObject, long userId) {
    List<String> entries = new ArrayList<>();
    entries.add(String.format(DELETE_ENTRY_TEMPLATE, oldObject.getClass().getSimpleName(), oldObject.getObjectDescriptiveName()));
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnUpdate(T objectToUpdate, T objectWithNewValues, long userId) {
    List<String> differences = objectToUpdate.getDifferences(objectWithNewValues);
    if (differences.isEmpty()) {
      return;
    }
    differences.add(0, String.format(CHANGES_TEMPLATE, objectToUpdate.getClass().getSimpleName(), objectToUpdate.getObjectDescriptiveName()));
    saveHistoryEntries(differences, userId);
  }

  private void saveHistoryEntries(List<String> entries, long userId) {
    HistoryEntry historyEntry = HistoryEntry.builder()
        .userId(userId)
        .date(LocalDateTime.now())
        .entry(entries)
        .build();
    historyEntryRepository.save(historyEntry);
  }
}

