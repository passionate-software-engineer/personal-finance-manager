package com.pfm.account;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {

  private AccountRepository accountRepository;

  public Account getAccountById(Long id) {
    return accountRepository.findById(id).orElse(null);
  }

  public List<Account> getAccounts() {
    return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Account::getId))
        .collect(Collectors.toList());
  }

  public Account addAccount(Account account) {
    return accountRepository.save(account);
  }

  public Account updateAccount(Long id, Account account) {
    Account accountToUpdate = getAccountById(id);
    accountToUpdate.setName(account.getName());
    accountToUpdate.setBalance(account.getBalance());
    accountRepository.save(accountToUpdate);
    return accountToUpdate;
  }

  public void deleteAccount(Long id) {
    accountRepository.deleteById(id);
  }
}
