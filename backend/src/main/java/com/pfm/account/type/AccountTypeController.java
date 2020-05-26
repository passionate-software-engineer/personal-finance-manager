package com.pfm.account.type;

import com.pfm.auth.UserProvider;
import com.pfm.history.HistoryEntryService;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AccountTypeController implements AccountTypeApi {

  private final AccountTypeService accountTypeService;
  private final AccountTypeValidator accountTypeValidator;
  private final UserProvider userProvider;
  private final HistoryEntryService historyEntryService;

  @Override
  public ResponseEntity<?> getAccountTypeById(@PathVariable long accountTypeId) {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving account type with id: {}", accountTypeId);

    Optional<AccountType> accountType = accountTypeService.getAccountTypeByIdAndUserId(accountTypeId, userId);

    if (!accountType.isPresent()) {
      log.info("Account type with id {} was not found", accountTypeId);
      return ResponseEntity.notFound().build();
    }
    log.info("Account type with id {} was successfully retrieved", accountTypeId);
    return ResponseEntity.ok(accountType.get());
  }

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

  @Override
  @Transactional
  public ResponseEntity<?> deleteAccountType(@PathVariable long accountTypeId) {
    long userId = userProvider.getCurrentUserId();
    Optional<AccountType> accountTypeOptional = accountTypeService.getAccountTypeByIdAndUserId(accountTypeId, userId);

    if (accountTypeOptional.isEmpty()) {
      log.info("No account type with id {} was found, not able to delete", accountTypeId);
      return ResponseEntity.notFound().build();
    }

    AccountType accountType = accountTypeOptional.get();
    log.info("Attempting to delete account type with id {}", accountTypeId);
    historyEntryService.addHistoryEntryOnDelete(accountType, userId);
    accountTypeService.deleteAccountType(accountTypeId);

    log.info("Account type with id {} was deleted successfully", accountTypeId);

    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateAccountType(@PathVariable long accountTypeId, @RequestBody AccountTypeRequest accountTypeRequest) {
    long userId = userProvider.getCurrentUserId();

    if (accountTypeService.getAccountTypeByIdAndUserId(accountTypeId, userId).isEmpty()) {
      log.info("No account type with id {} was found, not able to update", accountTypeId);
      return ResponseEntity.notFound().build();
    }

    AccountType accountType = convertAccountTypeRequestToAccountType(accountTypeRequest);

    log.info("Updating account type with id {}", accountTypeId);
    List<String> validationResult = accountTypeValidator.validateAccountTypeForUpdate(accountTypeId, userId, accountType);

    if (!validationResult.isEmpty()) {
      log.info("Account type is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    AccountType accountTypeToUpdate = accountTypeService.getAccountTypeByIdAndUserId(accountTypeId, userId).get();

    historyEntryService.addHistoryEntryOnUpdate(accountTypeToUpdate, accountType, userId);
    accountTypeService.updateAccountType(accountTypeId, userId, accountType);

    log.info("Account type with id {} was successfully updated", accountTypeId);

    return ResponseEntity.ok().build();
  }
}
