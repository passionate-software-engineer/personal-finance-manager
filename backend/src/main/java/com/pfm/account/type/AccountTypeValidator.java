package com.pfm.account.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountTypeValidator {

  private AccountTypeService accountTypeService;

  private List<String> validate(AccountType accountType) {
    List<String> validationResults = new ArrayList<>();

    if (accountType.getName() == null || accountType.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_ACCOUNT_NAME));
    }

    return validationResults;
  }

  public List<String> validateAccountTypeIncludingNameDuplication(long userId, AccountType accountType) {
    List<String> validationResults = validate(accountType);

    checkForDuplicatedName(userId, validationResults, accountType);

    return validationResults;
  }

  public List<String> validateAccountTypeForUpdate(long id, long userId, AccountType accountType) {
    Optional<AccountType> accountTypeToUpdate = accountTypeService.getAccountTypeIdAndUserId(id, userId);

    if (!accountTypeToUpdate.isPresent()) {
      throw new IllegalStateException("Account type with id: " + id + " does not exist in database");
    }

    // it's ok when we keep name in updated accountType, it's not duplicate
    if (accountTypeToUpdate.get().getName().equals(accountType.getName())) {
      return validate(accountType);
    }

    // it's not ok if accountType is duplicating name of other accountType
    return validateAccountTypeIncludingNameDuplication(userId, accountType);
  }

  private void checkForDuplicatedName(long userId, List<String> validationResults, AccountType accountType) {
    if (accountType.getName() != null && !accountType.getName().trim().equals("")
        && accountTypeService
        .isAccountTypeNameAlreadyUsed(userId, accountType.getName())) {
      validationResults.add(getMessage(ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }

}
