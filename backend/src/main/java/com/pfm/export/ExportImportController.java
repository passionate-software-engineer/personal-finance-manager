package com.pfm.export;

import static com.pfm.config.MessagesProvider.IMPORT_NOT_POSSIBLE;

import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.config.MessagesProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController // TODO add support for history - import data should be part of the history
public class ExportImportController implements ExportImportApi {

  private ExportService exportService;
  private ImportService importService;
  private UserProvider userProvider;
  private AccountService accountService;

  @Override
  public ExportResult exportData() {
    long userId = userProvider.getCurrentUserId();

    return exportService.exportData(userId);
  }

  @Override
  public ResponseEntity<?> importData(@RequestBody ExportResult inputData) {
    long userId = userProvider.getCurrentUserId();

    if (!accountService.getAccounts(userId).isEmpty()) {
      return ResponseEntity.badRequest().body(MessagesProvider.getMessage(IMPORT_NOT_POSSIBLE));
    }

    // TODO validate input - if all required fields are present

    try {
      importService.importData(inputData, userId);
    } catch (ImportFailedException ex) {
      return ResponseEntity.badRequest().body(ex.getMessage());
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
