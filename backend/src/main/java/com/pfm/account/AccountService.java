package com.pfm.account;

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
public class AccountService {

  private AccountRepository accountRepository;

  public Optional<Account> getAccountByIdAndUserId(long accountId, long userId) {
    return accountRepository.findByIdAndUserId(accountId, userId);
  }

  public Account getAccountFromDbByIdAndUserId(long accountId, long userId) {
    Optional<Account> accountByIdAndUserId = accountRepository.findByIdAndUserId(accountId, userId);
    if (!accountByIdAndUserId.isPresent()) {
      throw new IllegalStateException("Account with id: " + accountId + " does not exist in database");
    }
    return accountByIdAndUserId.get();
  }

  public List<Account> getAccounts(long userId) {
    return accountRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(Account::getId))
        .collect(Collectors.toList());
  }

  public Account saveAccount(long userId, Account account) {
    account.setUserId(userId);
    return accountRepository.save(account);
  }

  public void updateAccount(long accountId, long userId, Account account) {
    Optional<Account> accountFromDb = getAccountByIdAndUserId(accountId, userId);

    if (!accountFromDb.isPresent()) {
      throw new IllegalStateException("Account with id: " + accountId + " does not exist in database");
    }

    Account accountToUpdate = accountFromDb.get();
    accountToUpdate.setName(account.getName());
    accountToUpdate.setBalance(account.getBalance());
    accountToUpdate.setCurrency(account.getCurrency());
    accountToUpdate.setType(account.getType());

    accountRepository.save(accountToUpdate);
  }

  public void deleteAccount(long accountId) {
    accountRepository.deleteById(accountId);
  }

  public boolean isAccountNameAlreadyUsed(long userId, String name) {
    return accountRepository.findByNameIgnoreCaseAndUserId(name, userId).size() != 0;
  }

  public boolean accountDoesNotExistByIdAndUserId(long accountId, long userId) {
    return !accountRepository.existsByIdAndUserId(accountId, userId);
  }
}
