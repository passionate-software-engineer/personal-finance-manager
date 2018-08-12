package com.pfm.transaction;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

  private TransactionRepository transactionRepository;
  private AccountService accountService;

  public Optional<TransactionPojo> getTransactionById(long id) {
    return transactionRepository.findById(id);
  }

  public List<TransactionPojo> getTransactions() {
    return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(TransactionPojo::getId))
        .collect(Collectors.toList());
  }

  public TransactionPojo addTransaction(TransactionPojo transaction) {
    subtractAmountFromAccount(transaction.getAccount().getId(), transaction.getPrice());
    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!
    return transactionRepository.save(transaction);
  }

  public void updateTransaction(long id, TransactionPojo transaction) {
    TransactionPojo transactionToUpdate = getTransactionFromDatabase(id);
    transactionToUpdate.setDescription(transaction.getDescription());
    transactionToUpdate.setCategory(transaction.getCategory());
    transactionToUpdate.setPrice(transaction.getPrice());
    transactionToUpdate.setAccount(transaction.getAccount());
    transactionToUpdate.setDate(transaction.getDate());

    transactionRepository.save(transactionToUpdate);

    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!
    addAmountToAccount(transactionToUpdate.getAccount().getId(), transactionToUpdate.getPrice());
    subtractAmountFromAccount(transactionToUpdate.getAccount().getId(), transactionToUpdate.getPrice());
  }

  public void deleteTransaction(long id) {
    TransactionPojo transactionToDelete = getTransactionFromDatabase(id);
    transactionRepository.deleteById(id);

    // TODO - did you enabled transactions? account state should be not changed when transaction save is failing!!
    addAmountToAccount(transactionToDelete.getAccount().getId(), transactionToDelete.getPrice());
  }

  private TransactionPojo getTransactionFromDatabase(long id) {
    Optional<TransactionPojo> transactionFromDb = getTransactionById(id);

    if (!transactionFromDb.isPresent()) {
      throw new IllegalStateException("TransactionPojo with id: " + id + " not exist in database");
    }

    return transactionFromDb.get();
  }

  public boolean idExist(long id) {
    return transactionRepository.existsById(id);
  }

  private void addAmountToAccount(long accountId, BigDecimal amountToAdd) {
    updateAccountBalance(accountId, amountToAdd, BigDecimal::add);
  }

  private void subtractAmountFromAccount(long accountId, BigDecimal amountToAdd) {
    updateAccountBalance(accountId, amountToAdd, BigDecimal::subtract);
  }

  private void updateAccountBalance(long accountId, BigDecimal amount, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
    Optional<Account> account = accountService.getAccountById(accountId);

    if (!account.isPresent()) {
      throw new IllegalStateException("Account with id: " + accountId + " not exist in database");
    }

    Account accountToUpdate = account.get();
    // TODO maybe you can write query which updates only balance? that's common operation so does not make sense to update other values
    accountToUpdate.setBalance(operation.apply(accountToUpdate.getBalance(), amount));

    accountService.updateAccount(accountToUpdate.getId(), accountToUpdate);
  }

}
