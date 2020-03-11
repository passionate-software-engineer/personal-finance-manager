package com.pfm.account.type;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
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

    log.info("Returning list of account types for user " + userId);

    List<AccountType> accountType = accountTypeService.getAccountTypes(userId);

    return ResponseEntity.ok(accountType);
  }


  @Override
  @Transactional
  public ResponseEntity<?> updateAccountType(@PathVariable long accountTypeId, @RequestBody AccountTypeRequest accountTypeRequest) {
    long userId = userProvider.getCurrentUserId();

    if (accountTypeService.getAccountTypeIdAndUserId(accountTypeId, userId).isEmpty()) {
      log.info("No accountType with id {} was found, not able to update", accountTypeId);
      return ResponseEntity.notFound().build();
    }

    AccountType accountType = convertAccountTypeRequestToAccountType(accountTypeRequest);

    log.info("Updating accountType with id {}", accountTypeId);
    List<String> validationResult = accountTypeValidator.validateAccountTypeForUpdate(accountTypeId, userId, accountType);

    if (!validationResult.isEmpty()) {
      log.info("Account type is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    AccountType accountTypeToUpdate = accountTypeService.getAccountTypeIdAndUserId(accountTypeId, userId).get();

    historyEntryService.addHistoryEntryOnUpdate(accountTypeToUpdate, accountType, userId);
    accountTypeService.updateAccountType(accountTypeId, userId, accountType);

    log.info("Account type with id {} was successfully updated", accountTypeId);

    return ResponseEntity.ok().build();
  }

  private AccountType convertAccountTypeRequestToAccountType(AccountTypeRequest accountTypeRequest) {
    return AccountType.builder()
        .name(accountTypeRequest.getName())
        .build();
  }

}
