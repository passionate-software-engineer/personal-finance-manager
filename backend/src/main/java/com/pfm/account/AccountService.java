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

  public Optional<Account> getAccountById(long id) {
    return accountRepository.findById(id);
  }

  public List<Account> getAccounts() {
    return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Account::getId))
        .collect(Collectors.toList());
  }

  public Account addAccount(Account account) {
    return accountRepository.save(account);
  }

  public void updateAccount(long id, Account account) {
    Optional<Account> accountFromDb = getAccountById(id);

    if (!accountFromDb.isPresent()) {
      throw new IllegalStateException("Account with id: " + id + " does not exist in database");
    }

    Account accountToUpdate = accountFromDb.get();
    accountToUpdate.setName(account.getName());
    accountToUpdate.setBalance(account.getBalance());

    accountRepository.save(accountToUpdate);
  }

  public void deleteAccount(long id) {
    accountRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return accountRepository.existsById(id);
  }

  // TODO - add check if account name already exists (similar as in category) (use method below)

  public boolean isAccountNameAlreadyUsed(String name) {
    return accountRepository.findByNameContainingIgnoreCase(name).size() != 0;
  }
}