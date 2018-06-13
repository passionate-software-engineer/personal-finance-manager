package com.personalfinancemanager.services;

import com.personalfinancemanager.model.Account;

import java.util.List;

public interface AccountService {

  public Account getAccountById(Long id);

  public List<Account> getAccounts();

  public Account addAccount(Account account);

  public void deleteAccount(Long id, Account account);

  public void deleteAccount(Long id);

}
