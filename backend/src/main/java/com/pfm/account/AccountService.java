package com.pfm.account;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

  public List<Account> getAccounts(long userId) {
    return StreamSupport.stream(accountRepository.findByUserId(userId).spliterator(), false)
        .sorted(Comparator.comparing(Account::getId))
        .collect(Collectors.toList());
  }

  public Account addAccount(Account account) {
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

    accountRepository.save(accountToUpdate);
  }

  public void deleteAccount(long accountId) {
    accountRepository.deleteById(accountId);
  }

  public boolean isAccountNameAlreadyUsed(String name) {
    return accountRepository.findByNameIgnoreCase(name).size() != 0;
  }

}