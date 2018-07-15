package com.pfm.account;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

  public Account updateAccount(long id, Account account) {
    Account accountToUpdate = getAccountById(id).get();
    accountToUpdate.setName(account.getName());
    accountToUpdate.setBalance(account.getBalance());
    return accountRepository.save(accountToUpdate);
  }

  public void deleteAccount(long id) {
    accountRepository.deleteById(id);
  }

  public boolean idExist(long id) {
    return accountRepository.existsById(id);
  }
}
