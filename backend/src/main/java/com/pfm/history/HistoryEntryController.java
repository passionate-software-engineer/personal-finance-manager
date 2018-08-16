package com.pfm.history;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class HistoryEntryController implements HistoryEntryApi {

  private HistoryEntryService historyEntryService;

  @Override
  public ResponseEntity<List<HistoryEntry>> getHistory(@RequestAttribute(value = "userId") long userId) {
    log.info("Retrieving all history entries");
    return ResponseEntity.ok(historyEntryService.getHistoryEntries(userId));
  }

}