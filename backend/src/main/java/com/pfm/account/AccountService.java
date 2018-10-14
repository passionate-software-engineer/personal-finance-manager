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

  //TODO possibly replace this method everywhere to use only "get.......ByIdAndUserId(long id, long userId)" to make app safer ??
  //Its used sometimes in places where validation is done e.g. in validator
  public Optional<Account> getAccountById(long id) {
    return accountRepository.findById(id);
  }

  public Optional<Account> getAccountByIdAndUserId(long id, long userId) {
    return accountRepository.findByIdAndUserId(id, userId);
  }

  public List<Account> getAccounts(long userId) {
    return StreamSupport.stream(accountRepository.findByUserId(userId).spliterator(), false)
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

  public boolean isAccountNameAlreadyUsed(String name) {
    return accountRepository.findByNameIgnoreCase(name).size() != 0;
  }

}