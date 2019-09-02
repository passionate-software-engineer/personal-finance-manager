package com.pfm.planned.transaction;

import com.pfm.transaction.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannedTransactionRepository extends CrudRepository<Transaction, Long> {

  List<Transaction> findByUserId(long userId);

  Optional<Transaction> findByIdAndUserId(long plannedTransactionId, long userId);

  boolean existsByCategoryId(long categoryId);

  boolean existsByIdAndUserId(long plannedTransactionId, long userId);

}
