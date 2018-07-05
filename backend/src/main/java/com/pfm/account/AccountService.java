package com.pfm.account;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {

  @Autowired // TODO - it's better to do dependency injection through constructor - please change to it
  private AccountRepository accountRepository;

  public Account getAccountById(Long id) {
    return accountRepository.findById(id).get(); // TODO - it's not correct use of optional - you should return it further and then in controller check if value is present
  }

  public List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>(); // TODO better to use: StreamSupport.stream(iterable.spliterator(), false)
    accountRepository.findAll().forEach(account -> accounts.add(account));
    accounts.sort(Comparator.comparing(Account::getId)); // TODO when you will refactor to method above just use sorted() on stream and then collect
    return accounts;
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
