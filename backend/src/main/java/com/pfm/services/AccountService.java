package com.pfm.services;

import com.pfm.model.Account;
import com.pfm.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class AccountService {

  @Autowired
  private AccountRepository accountRepository;

  public Account getAccountById(Long id) {
    return accountRepository.findById(id).get();
  }

  public List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>();
    accountRepository.findAll().forEach(account -> accounts.add(account));
    accounts.sort(Comparator.comparing(Account::getId));
    return accounts;
  }

  public Account addAccount(Account account) {
    return accountRepository.save(account);
  }

  public void updateAccount(Long id, Account account) {
    accountRepository.deleteById(id);
    accountRepository.save(Account.builder().
        id(account.getId())
        .balance(account.getBalance())
        .name(account.getName())
        .build());
  }

  public void deleteAccount(Long id) {
    accountRepository.deleteById(id);
  }

}
