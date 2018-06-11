package services;

import repositories.AccountRepository;
import model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public Account getAccountById(Long id) {
    return accountRepository.findById(id).get();
  }

  @Override
  public List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>();
    accountRepository.findAll().forEach(account -> accounts.add(account));
    accounts.sort(Comparator.comparing(Account::getId));
    return accounts;
  }

  @Override
  public Account addAccount(Account account) {
    return accountRepository.save(account);
  }

  @Override
  public void deleteAccount(Long id, Account account) {
    accountRepository.deleteById(id);
    accountRepository.save(Account.builder().
      id(account.getId())
      .balance(account.getBalance())
      .accountNumber(account.getAccountNumber())
      .currency(account.getCurrency())
      .lastModifiedTS(LocalDateTime.now())
      .userId(account.getUserId())
      .build());

  }

  @Override
  public void deleteAccount(Long id) {
  accountRepository.deleteById(id);
  }
}
