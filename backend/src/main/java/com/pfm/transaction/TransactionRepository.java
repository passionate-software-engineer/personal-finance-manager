package com.pfm.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

  List<Transaction> findByUserId(long userId);

  Optional<Transaction> findByIdAndUserId(long transactionId, long userId);

  boolean existsByCategoryId(long categoryId);

  boolean existsByIdAndUserId(long transactionId, long userId);

  @Query("select new java.lang.String(transaction.importId)"
      + "from Transaction transaction  where transaction.userId =:id and transaction.importId is not null")
  Set<String> getAllImportIds(@Param("id") Long userId);

  @Query("select new java.lang.String(t.importId)from Transaction t  where t.userId =:id and t.importId"
      + " is not null and (t.date >= :from  and t.date <= :to )")
  Set<String> getImportIdsForDateRange(@Param("id") Long userId, LocalDate from, LocalDate to);
}
