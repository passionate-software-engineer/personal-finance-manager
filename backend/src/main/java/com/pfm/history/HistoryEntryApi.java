package com.pfm.history;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("history")
@CrossOrigin
@Api(tags = {"history-entry-controller"})
public interface HistoryEntryApi {

  // TODO add option to return history entries by date range
  @ApiOperation(value = "List history entries", response = Void.class, authorizations = {@Authorization(value = "Bearer")})
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = HistoryEntry.class, responseContainer = "list"),
      @ApiResponse(code = 400, message = "Bad request", response = Void.class),
  })
  @GetMapping
  ResponseEntity<List<HistoryEntry>> getHistory();

}
