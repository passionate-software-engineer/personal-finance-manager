package com.pfm.history;

import com.pfm.auth.UserProvider;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class HistoryEntryController implements HistoryEntryApi {

  private HistoryEntryService historyEntryService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<List<HistoryEntry>> getHistory() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all history entries");
    return ResponseEntity.ok(historyEntryService.getHistoryEntries(userId));
  }

}