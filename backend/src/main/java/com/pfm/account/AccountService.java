package com.pfm.account;

import java.math.BigDecimal;
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

  public Account addAccount(long userId, Account account) {
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

    accountRepository.save(accountToUpdate);
  }

  public void deleteAccount(long accountId) {
    accountRepository.deleteById(accountId);
  }

  public boolean isAccountNameAlreadyUsed(long userId, String name) {
    return accountRepository.findByNameIgnoreCaseAndUserId(name, userId).size() != 0;
  }

  public void updateAccountBalance(long accountId, BigDecimal newBalance) {
    accountRepository.updateAccountBalance(newBalance, accountId);
  }

  public boolean accountExistByIdAndUserId(long accountId, long userId) {
    return accountRepository.existsByIdAndUserId(accountId, userId);
  }
}