package com.pfm.account.type;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AccountTypeController implements AccountTypeApi {

  private AccountTypeService accountTypeService;
  private AccountTypeValidator accountTypeValidator;
  private UserProvider userProvider;
  private HistoryEntryService historyEntryService;

  @Override
  public ResponseEntity<List<AccountType>> getAccountTypes() {
    long userId = userProvider.getCurrentUserId();

    log.info("Returning list of account types for user {}", userId);

    List<AccountType> accountType = accountTypeService.getAccountTypes(userId);

    return ResponseEntity.ok(accountType);
  }

  @Override
  @Transactional
  public ResponseEntity<?> addAccountType(@RequestBody AccountTypeRequest accountTypeRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Saving accountType {} to the database", accountTypeRequest.getName());

    AccountType accountType = convertAccountTypeRequestToAccountType(accountTypeRequest);

    List<String> validationResult = accountTypeValidator.validateAccountTypeIncludingNameDuplication(userId, accountType);
    if (!validationResult.isEmpty()) {
      log.info("Account type is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    AccountType createdAccountType = accountTypeService.saveAccountType(userId, accountType);
    log.info("Saving accountType to the database was successful. Account Type id is {}", createdAccountType.getId());
    historyEntryService.addHistoryEntryOnAdd(createdAccountType, userId);
    return ResponseEntity.ok(createdAccountType.getId());
  }

  private AccountType convertAccountTypeRequestToAccountType(AccountTypeRequest accountTypeRequest) {
    return AccountType.builder()
        .name(accountTypeRequest.getName())
        .build();
  }

}
