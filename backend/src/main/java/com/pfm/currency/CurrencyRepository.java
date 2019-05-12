package com.pfm.currency;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Long> {

  List<Currency> findByUserId(long userId);

  Optional<Currency> findByIdAndUserId(long id, long userId);

}
