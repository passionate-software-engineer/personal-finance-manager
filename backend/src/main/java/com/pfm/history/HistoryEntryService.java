package com.pfm.history;

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

  private static final String ADD_ENTRY_TEMPLATE = "Added %s '%s'";
  private static final String DELETE_ENTRY_TEMPLATE = "Deleted %s '%s'";
  private static final String NO_CHANGE_TEMPLATE = "%s was updated but there were no changes";

  private HistoryEntryRepository historyEntryRepository;

  public List<HistoryEntry> getHistoryEntries(long userId) {
    return historyEntryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(HistoryEntry::getId))
        .collect(Collectors.toList());
  }

  // TODO refactor security to not force develeopers to pass usreId all around.
  public <T extends DifferenceProvider<T>> void addEntryOnAdd(T newObject, long userId) {
    List<String> entries = newObject.getObjectPropertiesWithValues();
    entries.add(0, String.format(ADD_ENTRY_TEMPLATE, newObject.getClass().getSimpleName(), newObject.getObjectDescriptiveName()));
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnDelete(T oldObject, long userId) {
    List<String> entries = oldObject.getObjectPropertiesWithValues();
    entries.add(String.format(DELETE_ENTRY_TEMPLATE, oldObject.getClass().getSimpleName(), oldObject.getObjectDescriptiveName()));
    saveHistoryEntries(entries, userId);
  }

  public <T extends DifferenceProvider<T>> void addEntryOnUpdate(T objectToUpdate, T objectWithNewValues, long userId) {
    List<String> differences = objectToUpdate.getDifferences(objectWithNewValues);
    if (differences.isEmpty()) {
      differences.add(String.format(NO_CHANGE_TEMPLATE, objectToUpdate.getClass().getSimpleName()));
    }
    saveHistoryEntries(differences, userId);
  }

  private void saveHistoryEntries(List<String> entries, long userId) {
    for (String entry : entries) {
      HistoryEntry historyEntry = HistoryEntry.builder()
          .userId(userId)
          .date(LocalDateTime.now())
          .entry(entry)
          .build();
      historyEntryRepository.save(historyEntry);
    }
  }

}