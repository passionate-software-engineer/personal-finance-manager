package com.pfm.planned.transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class PlannedTransactionService {

  private PlannedTransactionRepository plannedTransactionRepository;

  public Optional<PlannedTransaction> getPlannedTransactionByIdAndUserId(long id, long userId) {
    return plannedTransactionRepository.findByIdAndUserId(id, userId);
  }

  public List<PlannedTransaction> getPlannedTransactions(long userId) {
    return plannedTransactionRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(PlannedTransaction::getId))
        .collect(Collectors.toList());
  }

  @Transactional
  public PlannedTransaction addPlannedTransaction(long userId, PlannedTransaction plannedTransaction) {
    plannedTransaction.setUserId(userId);
    return plannedTransactionRepository.save(plannedTransaction);
  }

  @Transactional
  public void updatePlannedTransaction(long id, long userId, PlannedTransaction plannedTransaction) {
    PlannedTransaction plannedTransactionToUpdate = getPlannedTransactionThrowExceptionWhenNotExist(id, userId);

    plannedTransactionToUpdate.setDescription(plannedTransaction.getDescription());
    plannedTransactionToUpdate.setCategoryId(plannedTransaction.getCategoryId());
    plannedTransactionToUpdate.getAccountPriceEntries().clear();
    plannedTransactionToUpdate.getAccountPriceEntries().addAll(plannedTransaction.getAccountPriceEntries());
    plannedTransactionToUpdate.setDate(plannedTransaction.getDate());

    plannedTransactionRepository.save(plannedTransactionToUpdate);
  }

  @Transactional
  public void deletePlannedTransaction(long id, long userId) {
    getPlannedTransactionThrowExceptionWhenNotExist(id, userId);

    plannedTransactionRepository.deleteById(id);
  }

  private PlannedTransaction getPlannedTransactionThrowExceptionWhenNotExist(long id, long userId) {
    Optional<PlannedTransaction> plannedTransaction = getPlannedTransactionByIdAndUserId(id, userId);
    if (plannedTransaction.isEmpty()) {
      throw new IllegalStateException("Planned transaction with id: " + id + " does not exist in database");
    }
    return plannedTransaction.get();
  }

}
