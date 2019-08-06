package com.pfm.planned_transaction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PlannedTransactionRepository extends CrudRepository<PlannedTransaction, Long> {

  List<PlannedTransaction> findByUserId(long userId);

  Optional<PlannedTransaction> findByIdAndUserId(long plannedTransactionId, long userId);

  boolean existsByCategoryId(long categoryId);

  boolean existsByIdAndUserId(long plannedTransactionId, long userId);

}
