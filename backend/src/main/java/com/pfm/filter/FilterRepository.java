package com.pfm.filter;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepository extends CrudRepository<Filter, Long> {

  List<Filter> findByUserId(long userId);

  Optional<Filter> findByIdAndUserId(long id, long userId);

  boolean existsByAccountIdsContains(long accountId);

  boolean existsByCategoryIdsContains(long categoryId);

  boolean existsByIdAndUserId(long filterId, long userId);
}
