package com.pfm.transaction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

  List<Transaction> findByUserId(long userId);

  Optional<Transaction> findByIdAndUserId(long transactionId, long userId);

  boolean existsByCategoryId(long categoryId);

  boolean existsByIdAndUserId(long transactionId, long userId);
}

