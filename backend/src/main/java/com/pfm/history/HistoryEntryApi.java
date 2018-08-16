package com.pfm.history;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("history")
@CrossOrigin
@Api(value = "HistoryEntry", description = "Controller used to list history.")
public interface HistoryEntryApi {

  // TODO add option to return history entries by date range
  @ApiOperation(value = "List history entries", response = HistoryEntry.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<HistoryEntry>> getHistory(long userId);

}