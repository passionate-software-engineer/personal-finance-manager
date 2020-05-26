package com.pfm.account.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_ID_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AccountTypeService {

  private AccountTypeRepository accountTypeRepository;

  public Optional<AccountType> getAccountTypeByIdAndUserId(long accountTypeId, long userId) {
    return accountTypeRepository.findByIdAndUserId(accountTypeId, userId);
  }

  public AccountType getAccountTypeFromDbByIdAndUserId(long accountTypeId, long userId) {
    Optional<AccountType> accountTypeOptional = accountTypeRepository.findByIdAndUserId(accountTypeId, userId);
    if (!accountTypeOptional.isPresent()) {
      throw new IllegalStateException(String.format(getMessage(ACCOUNT_TYPE_ID_DOES_NOT_EXIST), accountTypeId));
    }
    return accountTypeOptional.get();
  }

  public List<AccountType> getAccountTypes(long userId) {
    return accountTypeRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(AccountType::getName))
        .collect(Collectors.toList());
  }

  public void updateAccountType(long accountTypeId, long userId, AccountType accountType) {
    Optional<AccountType> accountTypeFromDb = getAccountTypeByIdAndUserId(accountTypeId, userId);

    if (!accountTypeFromDb.isPresent()) {
      throw new IllegalStateException("Account type with id: " + accountTypeId + " does not exist in database");
    }

    AccountType accountTypeToUpdate = accountTypeFromDb.get();
    accountTypeToUpdate.setName(accountType.getName());

    accountTypeRepository.save(accountTypeToUpdate);
  }

  public AccountType saveAccountType(long userId, AccountType accountType) {
    accountType.setUserId(userId);
    return accountTypeRepository.save(accountType);
  }

  public void deleteAccountType(long accountTypeId) {
    accountTypeRepository.deleteById(accountTypeId);
  }

  public boolean isAccountTypeNameAlreadyUsed(long userId, String name) {
    return accountTypeRepository.findByNameIgnoreCaseAndUserId(name, userId).size() != 0;
  }

  public List<AccountType> getDefaultAccountTypes(long userId) {
    return Arrays.asList(
        AccountType.builder().name("Personal").userId(userId).build(),
        AccountType.builder().name("Investment").userId(userId).build(),
        AccountType.builder().name("Saving").userId(userId).build(),
        AccountType.builder().name("Credit").userId(userId).build()
    );
  }

  public void addDefaultAccountTypes(long userId) {
    getDefaultAccountTypes(userId).forEach(accountTypeRepository::save);
  }
}
