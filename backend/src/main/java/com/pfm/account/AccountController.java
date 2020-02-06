package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.type.AccountTypeService;
import com.pfm.auth.UserProvider;
import com.pfm.currency.CurrencyService;
import com.pfm.history.HistoryEntryService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
public class AccountController implements AccountApi {

  private static final boolean SET_ACCOUNT_AS_ARCHIVED = true;
  private static final boolean SET_ACCOUNT_AS_ACTIVE = false;

  private AccountService accountService;
  private AccountValidator accountValidator;
  private HistoryEntryService historyEntryService;
  private CurrencyService currencyService;
  private AccountTypeService accountTypeService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<?> getAccountById(@PathVariable long accountId) {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving account with id: {}", accountId);

    Optional<Account> account = accountService.getAccountByIdAndUserId(accountId, userId);

    if (!account.isPresent()) {
      log.info("Account with id {} was not found", accountId);
      return ResponseEntity.notFound().build();
    }
    log.info("Account with id {} was successfully retrieved", accountId);
    return ResponseEntity.ok(account.get());
  }

  @Override
  public ResponseEntity<List<Account>> getAccounts() {
    long userId = userProvider.getCurrentUserId();

    log.info("Retrieving all accounts from database");

    List<Account> accounts = accountService.getAccounts(userId);
    return ResponseEntity.ok(accounts);
  }

  @Override
  @Transactional
  public ResponseEntity<?> addAccount(@RequestBody AccountRequest accountRequest) {
    long userId = userProvider.getCurrentUserId();

    log.info("Saving account {} to the database", accountRequest.getName());

    // need to do validation before conversion of request as it will throw error otherwise
    if (isProvidedCurrencyIdIncorrect(accountRequest, userId)) {
      return returnBadRequestCurrencyDoesNotExist(accountRequest);
    }
    if (isProvidedAccountTypeIdIncorrect(accountRequest, userId)) {
      return returnBadRequestAccountTypeDoesNotExist(accountRequest);
    }

    Account account = convertAccountRequestToAccount(accountRequest, userId);

    List<String> validationResult = accountValidator.validateAccountIncludingNameDuplication(userId, account);
    if (!validationResult.isEmpty()) {
      log.info("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account createdAccount = accountService.saveAccount(userId, account);
    log.info("Saving account to the database was successful. Account id is {}", createdAccount.getId());
    historyEntryService.addHistoryEntryOnAdd(createdAccount, userId);
    return ResponseEntity.ok(createdAccount.getId());
  }

  @Override
  @Transactional
  public ResponseEntity<?> updateAccount(@PathVariable long accountId, @RequestBody AccountRequest accountRequest) {
    long userId = userProvider.getCurrentUserId();

    if (accountService.getAccountByIdAndUserId(accountId, userId).isEmpty()) {
      log.info("No account with id {} was found, not able to update", accountId);
      return ResponseEntity.notFound().build();
    }

    // need to do validation before conversion of request as it will throw error otherwise
    if (isProvidedCurrencyIdIncorrect(accountRequest, userId)) {
      return returnBadRequestCurrencyDoesNotExist(accountRequest);
    }
    if (isProvidedAccountTypeIdIncorrect(accountRequest, userId)) {
      return returnBadRequestAccountTypeDoesNotExist(accountRequest);
    }

    Account account = convertAccountRequestToAccount(accountRequest, userId);

    log.info("Updating account with id {}", accountId);
    List<String> validationResult = accountValidator.validateAccountForUpdate(accountId, userId, account);

    if (!validationResult.isEmpty()) {
      log.info("Account is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Account accountToUpdate = accountService.getAccountByIdAndUserId(accountId, userId).get();

    historyEntryService.addHistoryEntryOnUpdate(accountToUpdate, account, userId);
    accountService.updateAccount(accountId, userId, account);

    log.info("Account with id {} was successfully updated", accountId);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> markAccountAsVerifiedToday(long accountId) {
    long userId = userProvider.getCurrentUserId();

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to update", accountId);
      return ResponseEntity.notFound().build();
    }

    Account account = accountService.getAccountByIdAndUserId(accountId, userId).get();
    account.setLastVerificationDate(LocalDate.now());

    accountService.saveAccount(userId, account);

    // TODO add history entry on confirming account state

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> markAccountAsArchived(long accountId) {
    final boolean updateToBeApplied = SET_ACCOUNT_AS_ARCHIVED;

    long userId = userProvider.getCurrentUserId();
    Optional<Account> accountOptional = accountService.getAccountByIdAndUserId(accountId, userId);
    if (accountOptional.isEmpty()) {
      log.info("No account with id {} was found, not able to set as archived", accountId);
      return ResponseEntity.notFound().build();
    }
    Account accountToUpdate = accountOptional.get();
    Account account = getNewAccountInstanceWithUpdateApplied(accountToUpdate, updateToBeApplied);

    historyEntryService.addHistoryEntryOnUpdate(accountToUpdate, account, userId);
    return performUpdate(accountId, userId, updateToBeApplied);
  }

  @Override
  public ResponseEntity<?> markAccountAsActive(long accountId) {
    final boolean updateToBeApplied = SET_ACCOUNT_AS_ACTIVE;

    long userId = userProvider.getCurrentUserId();
    Optional<Account> optionalAccount = accountService.getAccountByIdAndUserId(accountId, userId);

    if (optionalAccount.isEmpty()) {
      log.info("No account with id {} was found, not able to set as active", accountId);
      return ResponseEntity.notFound().build();
    }
    Account accountToUpdate = optionalAccount.get();
    Account account = getNewAccountInstanceWithUpdateApplied(accountToUpdate, updateToBeApplied);

    historyEntryService.addHistoryEntryOnUpdate(accountToUpdate, account, userId);
    return performUpdate(accountId, userId, updateToBeApplied);
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteAccount(@PathVariable long accountId) {
    long userId = userProvider.getCurrentUserId();

    if (!accountService.getAccountByIdAndUserId(accountId, userId).isPresent()) {
      log.info("No account with id {} was found, not able to delete", accountId);
      return ResponseEntity.notFound().build();
    }

    List<String> validationResults = accountValidator.validateAccountForDelete(accountId);
    if (!validationResults.isEmpty()) {
      log.info("Account with id {} was found, in transaction or filter, not able to delete", accountId);
      return ResponseEntity.badRequest().body(validationResults);
    }

    Account account = accountService.getAccountByIdAndUserId(accountId, userId).get();
    historyEntryService.addHistoryEntryOnDelete(account, userId);
    log.info("Attempting to delete account with id {}", accountId);
    accountService.deleteAccount(accountId);

    log.info("Account with id {} was deleted successfully", accountId);

    return ResponseEntity.ok().build();
  }

  private Account getNewAccountInstanceWithUpdateApplied(Account accountToUpdate, boolean archive) {
    return Account.builder()
        .name(accountToUpdate.getName())
        .balance(accountToUpdate.getBalance())
        .currency(accountToUpdate.getCurrency())
        .lastVerificationDate(accountToUpdate.getLastVerificationDate())
        .archived(archive ? SET_ACCOUNT_AS_ARCHIVED : SET_ACCOUNT_AS_ACTIVE)
        .build();
  }

  private Account convertAccountRequestToAccount(AccountRequest accountRequest, long userId) {
    return Account.builder()
        .name(accountRequest.getName())
        .balance(accountRequest.getBalance())
        .currency(currencyService.getCurrencyByIdAndUserId(accountRequest.getCurrencyId(), userId))
        .type(accountTypeService.getAccountTypeByIdAndUserId(accountRequest.getAccountTypeId(), userId))
        .build();
  }

  private boolean isProvidedCurrencyIdIncorrect(@RequestBody AccountRequest accountRequest, long userId) {
    if (currencyService.findCurrencyByIdAndUserId(accountRequest.getCurrencyId(), userId).isEmpty()) {
      log.info("No currency with id {} was found, not able to update", accountRequest.getCurrencyId());
      return true;
    }
    return false;
  }

  private ResponseEntity<?> returnBadRequestCurrencyDoesNotExist(@RequestBody AccountRequest accountRequest) {
    return ResponseEntity.badRequest()
        .body(Collections.singletonList(String.format(getMessage(ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST), accountRequest.getCurrencyId())));
  }

  private boolean isProvidedAccountTypeIdIncorrect(@RequestBody AccountRequest accountRequest, long userId) {
    if (accountTypeService.findAccountTypeByIdAndUserId(accountRequest.getAccountTypeId(), userId).isEmpty()) {
      log.info("No account type with id {} was found, not able to update", accountRequest.getAccountTypeId());
      return true;
    }
    return false;
  }

  private ResponseEntity<?> returnBadRequestAccountTypeDoesNotExist(@RequestBody AccountRequest accountRequest) {
    return ResponseEntity.badRequest()
        .body(Collections.singletonList(String.format(getMessage(ACCOUNT_TYPE_ID_DOES_NOT_EXIST), accountRequest.getAccountTypeId())));
  }

  private ResponseEntity<?> performUpdate(long accountId, long userId, boolean shouldArchive) {
    Optional<Account> account = accountService.getAccountByIdAndUserId(accountId, userId);

    log.info("Attempting to set account status as {} with id {} ", shouldArchive ? "archived" : "active", accountId);
    account.get().setArchived(shouldArchive);
    accountService.saveAccount(userId, account.get());
    return ResponseEntity.ok().build();

  }
}
